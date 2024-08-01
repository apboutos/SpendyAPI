package com.apboutos.spendy.spendyapi.controller;

import com.apboutos.spendy.spendyapi.dto.EntryDTO;
import com.apboutos.spendy.spendyapi.exception.CategoryNotFoundException;
import com.apboutos.spendy.spendyapi.exception.UsernameNotFoundException;
import com.apboutos.spendy.spendyapi.response.entry.CreateEntriesResponse;
import com.apboutos.spendy.spendyapi.response.entry.DeleteEntriesResponse;
import com.apboutos.spendy.spendyapi.response.entry.UpdateEntriesResponse;
import com.apboutos.spendy.spendyapi.model.Category;
import com.apboutos.spendy.spendyapi.model.Entry;
import com.apboutos.spendy.spendyapi.service.EntryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1/entries", produces = "application/json")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class EntryController {

    private final EntryService entryService;

    /**
     * Endpoint for the retrieval of entries that returns all the entries that were updated after the specified timestamp.
     * @param lastPullRequestTimestamp a string containing an {@code ISO-8601} timestamp of the last time the client performed a pull request.
     * @return a list of {@link EntryDTO} objects containing all the retrieved entries.
     */
    @GetMapping
    ResponseEntity<List<EntryDTO>> getEntries(@RequestParam("lastPullRequestTimestamp") @NotBlank String lastPullRequestTimestamp) throws UsernameNotFoundException {
        log.info("Called getEntries with {}", lastPullRequestTimestamp);

        final LocalDateTime localDateTime = LocalDateTime.parse(lastPullRequestTimestamp, DateTimeFormatter.ISO_DATE_TIME);
        final List<EntryDTO> entries = entryService.getEntries(Timestamp.valueOf(localDateTime),"apboutos@gmail.com");

        return new ResponseEntity<>(entries, HttpStatus.OK);
    }

    /**
     * Endpoint for the retrieval of entries that returns all the entries that were created at the specified date.
     * @param date a string containing an {@code ISO-8601} date to be matches the creation date of the entries.
     * @return a list of {@link EntryDTO} objects containing all the retrieved entries.
     */
    @GetMapping(path = "/date")
    ResponseEntity<List<EntryDTO>> getEntriesByDate(@RequestParam("date") @NotBlank String date) throws UsernameNotFoundException {
        log.info("Called getEntriesByDate with date {}", date);

        final LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE_TIME);
        final Date parsedDate = Date.valueOf(localDate);

        final List<EntryDTO> entries = entryService.getEntriesByDate(parsedDate,"apboutos@gmail.com");

        return new ResponseEntity<>(entries,HttpStatus.OK);
    }

    @GetMapping(path = "/aggregates/by-category")
   ResponseEntity<Map<UUID, List<Integer>>> getAggregatesByCategory(
            @RequestParam("categories") @NotNull List<UUID> categories,
            @RequestParam("date") @NotNull String date) throws UsernameNotFoundException {
        log.info("Called getAggregatesByCategory with categories: {} date: {}",categories,date);

        final LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE_TIME);

        final Map<UUID, List<Integer>> result = this.entryService.getPriceSumByDate("apboutos@gmail.com",categories,localDate.getDayOfMonth(), localDate.getMonthValue(), localDate.getYear());

        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    /**
     * Endpoint for creating the specified entries.
     * @param entries a list of {@link EntryDTO} objects containing all the data of the entries to be created.
     * @return a {@link CreateEntriesResponse} containing the created entries as well as entries that failed to be created due to a conflict.
     */
    @PostMapping
    ResponseEntity<CreateEntriesResponse> createEntries(@Valid @RequestBody List<EntryDTO> entries) throws UsernameNotFoundException {
        log.info("Called createEntries with {}", entries);

        final CreateEntriesResponse response = entryService.saveEntries(entries,"apboutos@gmail.com");
        if (response.getConflictingEntriesOnId().isEmpty() && response.getConflictingEntriesOnCategory().isEmpty()) {
            return new ResponseEntity<>(response,HttpStatus.CREATED);
        }
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    /**
     * Endpoint for updating the specified entries.
     * @param entries a list of {@link EntryDTO} objects containing all the data of the entries to be updated.
     * @return a {@link UpdateEntriesResponse} containing the updated entries as well as entries that failed to be created due to a conflict.
     */
    @PutMapping
    ResponseEntity<UpdateEntriesResponse> updateEntries(@Valid @RequestBody List<EntryDTO> entries){
        log.info("Called updateEntries with {}", entries);

        final UpdateEntriesResponse response = entryService.updateEntries(entries);

        if (response.getConflictingEntriesOnId().isEmpty()
                && response.getConflictingEntriesOnCategory().isEmpty()
                && response.getConflictingEntriesOnLastUpdate().isEmpty()) {
            return new ResponseEntity<>(response,HttpStatus.OK);
        }
        return new ResponseEntity<>(response,HttpStatus.CONFLICT);
    }

    /**
     * Endpoint for updating the category of all entries.
     * @param oldCategoryUUID the {@link UUID} of the category to be replaced.
     * @param newCategoryUUID the {@link UUID} of the replacement category.
     * @return a {@link UpdateEntriesResponse} containing the entries that were updated.
     * @throws CategoryNotFoundException if either the category to be replaced or the replacement category were not found.
     */
    @PutMapping(path = "/replaceCategory")
    ResponseEntity<UpdateEntriesResponse> replaceCategory(
            @RequestParam @NotBlank String oldCategoryUUID,
            @RequestParam @NotBlank String newCategoryUUID) throws CategoryNotFoundException, UsernameNotFoundException {
        log.info("Called replaceCategory with oldCategoryUUID {} and newCategoryUUID {}", oldCategoryUUID, newCategoryUUID);

        final List<EntryDTO> updatedEntries = entryService.replaceCategory(oldCategoryUUID, newCategoryUUID, "apboutos@gmail.com");

        return new ResponseEntity<>(new UpdateEntriesResponse(
                updatedEntries,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()),
                HttpStatus.OK);
    }

    /**
     * Endpoint for deleting all entries of a certain category.
     *
     * @param entryUUIDs a list containing the {@link UUID} of the {@link Entry} objects to be deleted.
     * @return a {@link ResponseEntity} containing the status {@code 204 (NO_CONTENT)} if the entries were deleted.
     */
    @DeleteMapping
    ResponseEntity<DeleteEntriesResponse> deleteEntries(@RequestParam List<UUID> entryUUIDs){
        log.info("Called deleteEntries with uuids {}", entryUUIDs);

        final DeleteEntriesResponse response = entryService.deleteEntries(entryUUIDs);

        return new ResponseEntity<>(response,response.getStatus());
    }

    /**
     * Endpoint for deleting all entries of a certain category.
     *
     * @param categoryUUID a string containing the {@link UUID} of the specified {@link Category}
     * @return a {@link ResponseEntity} containing the status {@code 204 (NO_CONTENT)} if the entries were deleted.
     * @throws CategoryNotFoundException if the specified category was not found when attempting to delete its entries.
     */
    @DeleteMapping(path = "/category")
    ResponseEntity<Object> deleteEntriesByCategory(@RequestParam @NotBlank String categoryUUID) throws CategoryNotFoundException {
        log.info("Called deleteEntriesByCategory with categoryUUID {}", categoryUUID);

        entryService.deleteEntriesByCategory(categoryUUID);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



}
