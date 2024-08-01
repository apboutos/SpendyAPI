package com.apboutos.spendy.spendyapi.response.entry;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;


@Data
@ResponseStatus()
public class DeleteEntriesResponse {

        private final HttpStatus status;
        private final boolean result;
        private final String message;
        private final Timestamp timestamp;
        private final List<UUID> conflictingEntries;

}
