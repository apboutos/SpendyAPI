package com.apboutos.spendy.spendyapi.response.entry;

import com.apboutos.spendy.spendyapi.dto.EntryDTO;
import lombok.Data;

import java.util.List;

@Data
public class CreateEntriesResponse {

    private final List<EntryDTO> savedEntries;
    private final List<EntryDTO> conflictingEntriesOnId;
    private final List<EntryDTO> conflictingEntriesOnCategory;

}
