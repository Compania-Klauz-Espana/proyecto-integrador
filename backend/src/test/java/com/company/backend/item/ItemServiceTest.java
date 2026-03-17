package com.company.backend.item;

import com.company.backend.item.dto.CreateItemRequest;
import com.company.backend.item.dto.ItemDTO;
import com.company.backend.item.dto.UpdateItemRequest;
import com.company.backend.shared.exception.ResourceNotFoundException;
import io.micrometer.core.instrument.Counter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @Mock
    private ItemRepository repository;

    @Mock
    private Counter itemsCreatedCounter;

    @Mock
    private Counter itemsDeletedCounter;

    private ItemService service;

    @BeforeEach
    void setUp() {
        service = new ItemService(repository, itemsCreatedCounter, itemsDeletedCounter);
    }

    @Test
    void findAll_returnsPageOfItems() {
        Item item = createItem(1L, "Test Item", ItemStatus.ACTIVE);
        Page<Item> page = new PageImpl<>(List.of(item));
        when(repository.findAll(any(Pageable.class))).thenReturn(page);

        Page<ItemDTO> result = service.findAll(PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals("Test Item", result.getContent().get(0).name());
    }

    @Test
    void findByStatus_filtersCorrectly() {
        Item item = createItem(1L, "Active Item", ItemStatus.ACTIVE);
        Page<Item> page = new PageImpl<>(List.of(item));
        when(repository.findByStatus(eq(ItemStatus.ACTIVE), any(Pageable.class))).thenReturn(page);

        Page<ItemDTO> result = service.findByStatus(ItemStatus.ACTIVE, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals(ItemStatus.ACTIVE, result.getContent().get(0).status());
    }

    @Test
    void findById_existingItem_returnsItem() {
        Item item = createItem(1L, "Test Item", ItemStatus.ACTIVE);
        when(repository.findById(1L)).thenReturn(Optional.of(item));

        ItemDTO result = service.findById(1L);

        assertEquals("Test Item", result.name());
    }

    @Test
    void findById_nonExisting_throwsException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
    }

    @Test
    void create_savesAndReturnsItem() {
        CreateItemRequest request = new CreateItemRequest("New Item", "Desc", ItemStatus.ACTIVE);
        Item saved = createItem(1L, "New Item", ItemStatus.ACTIVE);
        when(repository.save(any(Item.class))).thenReturn(saved);

        ItemDTO result = service.create(request);

        assertEquals("New Item", result.name());
        verify(itemsCreatedCounter).increment();
    }

    @Test
    void update_existingItem_updatesFields() {
        Item item = createItem(1L, "Old Name", ItemStatus.ACTIVE);
        when(repository.findById(1L)).thenReturn(Optional.of(item));
        when(repository.save(any(Item.class))).thenReturn(item);

        UpdateItemRequest request = new UpdateItemRequest("New Name", null, ItemStatus.INACTIVE);
        ItemDTO result = service.update(1L, request);

        assertEquals("New Name", result.name());
        assertEquals(ItemStatus.INACTIVE, result.status());
    }

    @Test
    void update_nonExisting_throwsException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.update(99L, new UpdateItemRequest("Name", null, null)));
    }

    @Test
    void delete_existingItem_deletesSuccessfully() {
        when(repository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).deleteById(1L);
        verify(itemsDeletedCounter).increment();
    }

    @Test
    void delete_nonExisting_throwsException() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(99L));
    }

    private Item createItem(Long id, String name, ItemStatus status) {
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        item.setDescription("Description");
        item.setStatus(status);
        item.setCreatedAt(LocalDateTime.now());
        item.setUpdatedAt(LocalDateTime.now());
        return item;
    }
}
