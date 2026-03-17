package com.company.backend.item.dto;

import com.company.backend.item.ItemStatus;
import java.time.LocalDateTime;

public record ItemDTO(
        Long id,
        String name,
        String description,
        ItemStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
