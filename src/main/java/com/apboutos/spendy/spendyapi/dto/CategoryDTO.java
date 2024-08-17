package com.apboutos.spendy.spendyapi.dto;

import com.apboutos.spendy.spendyapi.model.Type;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record CategoryDTO(
        @NotNull UUID uuid,
        @NotBlank String name,
        @NotNull Type type,
        @NotNull Instant date,
        @NotNull Instant lastUpdate,
        @NotNull boolean isDeleted) {

}
