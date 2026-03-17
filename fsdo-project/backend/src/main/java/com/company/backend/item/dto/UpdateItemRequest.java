package com.company.backend.item.dto;

import com.company.backend.item.ItemStatus;
import jakarta.validation.constraints.Size;

public record UpdateItemRequest(
        @Size(max = 255) String name,
        String description,
        ItemStatus status
) {}
