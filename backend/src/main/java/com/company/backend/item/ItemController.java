package com.company.backend.item;
// Klauz DevOps Project — REST API v1
import com.company.backend.item.dto.CreateItemRequest;
import com.company.backend.item.dto.ItemDTO;
import com.company.backend.item.dto.UpdateItemRequest;
import com.company.backend.shared.dto.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService service;

    public ItemController(ItemService service) {
        this.service = service;
    }

    @GetMapping
    public PageResponse<ItemDTO> findAll(
            @RequestParam(required = false) ItemStatus status,
            Pageable pageable) {
        Page<ItemDTO> page = (status != null)
                ? service.findByStatus(status, pageable)
                : service.findAll(pageable);
        return PageResponse.from(page);
    }

    @GetMapping("/{id}")
    public ItemDTO findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDTO create(@Valid @RequestBody CreateItemRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public ItemDTO update(@PathVariable Long id, @Valid @RequestBody UpdateItemRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
