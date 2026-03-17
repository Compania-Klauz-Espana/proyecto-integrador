package com.company.backend.item;

import com.company.backend.item.dto.CreateItemRequest;
import com.company.backend.item.dto.ItemDTO;
import com.company.backend.item.dto.UpdateItemRequest;
import com.company.backend.shared.exception.ResourceNotFoundException;
import io.micrometer.core.instrument.Counter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItemService {

    private final ItemRepository repository;
    private final Counter itemsCreatedCounter;
    private final Counter itemsDeletedCounter;

    public ItemService(ItemRepository repository,
                       Counter itemsCreatedCounter,
                       Counter itemsDeletedCounter) {
        this.repository = repository;
        this.itemsCreatedCounter = itemsCreatedCounter;
        this.itemsDeletedCounter = itemsDeletedCounter;
    }

    @Transactional(readOnly = true)
    public Page<ItemDTO> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(ItemMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<ItemDTO> findByStatus(ItemStatus status, Pageable pageable) {
        return repository.findByStatus(status, pageable).map(ItemMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public ItemDTO findById(Long id) {
        return repository.findById(id)
                .map(ItemMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
    }

    @Transactional
    public ItemDTO create(CreateItemRequest request) {
        Item item = ItemMapper.toEntity(request);
        Item saved = repository.save(item);
        itemsCreatedCounter.increment();
        return ItemMapper.toDTO(saved);
    }

    @Transactional
    public ItemDTO update(Long id, UpdateItemRequest request) {
        Item item = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));

        if (request.name() != null) {
            item.setName(request.name());
        }
        if (request.description() != null) {
            item.setDescription(request.description());
        }
        if (request.status() != null) {
            item.setStatus(request.status());
        }

        return ItemMapper.toDTO(repository.save(item));
    }

    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Item not found with id: " + id);
        }
        repository.deleteById(id);
        itemsDeletedCounter.increment();
    }
}
