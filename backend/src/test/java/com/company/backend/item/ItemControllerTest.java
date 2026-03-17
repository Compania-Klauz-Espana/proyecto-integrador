package com.company.backend.item;

import com.company.backend.item.dto.CreateItemRequest;
import com.company.backend.item.dto.ItemDTO;
import com.company.backend.item.dto.UpdateItemRequest;
import com.company.backend.shared.exception.GlobalExceptionHandler;
import com.company.backend.shared.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@Import(GlobalExceptionHandler.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService service;

    @Test
    void findAll_returnsPage() throws Exception {
        ItemDTO dto = createDTO(1L, "Item 1", ItemStatus.ACTIVE);
        Page<ItemDTO> page = new PageImpl<>(List.of(dto));
        when(service.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Item 1"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void findById_existing_returnsItem() throws Exception {
        ItemDTO dto = createDTO(1L, "Item 1", ItemStatus.ACTIVE);
        when(service.findById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Item 1"));
    }

    @Test
    void findById_nonExisting_returns404() throws Exception {
        when(service.findById(99L)).thenThrow(new ResourceNotFoundException("Item not found with id: 99"));

        mockMvc.perform(get("/api/items/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Item not found with id: 99"));
    }

    @Test
    void create_validRequest_returns201() throws Exception {
        CreateItemRequest request = new CreateItemRequest("New Item", "Desc", ItemStatus.ACTIVE);
        ItemDTO dto = createDTO(1L, "New Item", ItemStatus.ACTIVE);
        when(service.create(any(CreateItemRequest.class))).thenReturn(dto);

        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Item"));
    }

    @Test
    void create_invalidRequest_returns400() throws Exception {
        String invalidJson = "{\"name\":\"\",\"status\":null}";

        mockMvc.perform(post("/api/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_validRequest_returnsUpdated() throws Exception {
        UpdateItemRequest request = new UpdateItemRequest("Updated", null, ItemStatus.INACTIVE);
        ItemDTO dto = createDTO(1L, "Updated", ItemStatus.INACTIVE);
        when(service.update(eq(1L), any(UpdateItemRequest.class))).thenReturn(dto);

        mockMvc.perform(put("/api/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void delete_existing_returns204() throws Exception {
        mockMvc.perform(delete("/api/items/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_nonExisting_returns404() throws Exception {
        doThrow(new ResourceNotFoundException("Item not found with id: 99"))
                .when(service).delete(99L);

        mockMvc.perform(delete("/api/items/99"))
                .andExpect(status().isNotFound());
    }

    private ItemDTO createDTO(Long id, String name, ItemStatus status) {
        return new ItemDTO(id, name, "Description", status, LocalDateTime.now(), LocalDateTime.now());
    }
}
