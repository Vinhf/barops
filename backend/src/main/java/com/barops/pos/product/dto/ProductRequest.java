package com.barops.pos.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProductRequest(
        @NotBlank @Size(max = 150) String name,
        @Size(max = 150) String displayNameEn,
        Boolean active
) {
}
