package com.company.backend.item;

import com.company.backend.item.dto.CreateItemRequest;
import com.company.backend.item.dto.ItemDTO;

public final class ItemMapper {

    private ItemMapper() {}

    public static ItemDTO toDTO(Item item) {
        return new ItemDTO(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getStatus(),
                item.getCreatedAt(),
                item.getUpdatedAt()
        );
    }

    public static Item toEntity(CreateItemRequest request) {
        Item item = new Item();
        item.setName(request.name());
        item.setDescription(request.description());
        item.setStatus(request.status());
        return item;
    }
}
