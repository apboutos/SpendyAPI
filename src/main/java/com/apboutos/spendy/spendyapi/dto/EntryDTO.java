package com.apboutos.spendy.spendyapi.dto;

import com.apboutos.spendy.spendyapi.model.Type;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.UUID;




public record EntryDTO(
        @NotNull UUID uuid,
        @NotNull Type type,
        @NotNull UUID category,
        @NotBlank String description,
        @NotNull long price,
        @NotNull Date date,
        @NotNull Timestamp lastUpdate,
        @NotNull boolean isDeleted
) {

}

