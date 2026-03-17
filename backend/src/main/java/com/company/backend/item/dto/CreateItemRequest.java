package com.company.backend.item.dto;

import com.company.backend.item.ItemStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateItemRequest(
        @NotBlank @Size(max = 255) String name,
        String description,
        @NotNull ItemStatus status
) {}
