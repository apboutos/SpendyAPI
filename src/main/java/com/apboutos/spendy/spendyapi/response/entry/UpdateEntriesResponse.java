package com.apboutos.spendy.spendyapi.response.entry;

import com.apboutos.spendy.spendyapi.dto.EntryDTO;
import lombok.Data;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Data
@ResponseStatus()
public class UpdateEntriesResponse {

    private final List<EntryDTO> updatedEntries;
    private final List<EntryDTO> conflictingEntriesOnId;
    private final List<EntryDTO> conflictingEntriesOnCategory;
    private final List<EntryDTO> conflictingEntriesOnLastUpdate;

}
