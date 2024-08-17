package com.apboutos.spendy.spendyapi.dto;

import com.apboutos.spendy.spendyapi.model.Type;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;




public record EntryDTO(
        @NotNull UUID uuid,
        @NotNull Type type,
        @NotNull UUID category,
        @NotBlank String description,
        @NotNull long price,
        @NotNull Instant date,
        @NotNull Instant lastUpdate,
        @NotNull boolean isDeleted
) {

}

