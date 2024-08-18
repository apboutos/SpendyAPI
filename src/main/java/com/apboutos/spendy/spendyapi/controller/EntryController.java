package com.apboutos.spendy.spendyapi.controller;

import com.apboutos.spendy.spendyapi.dto.EntryDTO;
import com.apboutos.spendy.spendyapi.exception.CategoryNotFoundException;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
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
     * @param authentication the user authentication information.
     * @return a list of {@link EntryDTO} objects containing all the retrieved entries.
     */
    @GetMapping
    ResponseEntity<List<EntryDTO>> getEntries(@RequestParam("lastPullRequestTimestamp") @NotBlank String lastPullRequestTimestamp, Authentication authentication) {
        log.info("Called getEntries with {}", lastPullRequestTimestamp);

        final List<EntryDTO> entries = entryService.getEntries(Instant.parse(lastPullRequestTimestamp),authentication.getName());

        return new ResponseEntity<>(entries, HttpStatus.OK);
    }

    /**
     * Endpoint for the retrieval of entries that returns all the entries that were created within the specified moment range.
     * @param startingDate a string containing an {@code ISO-8601} date. Retrieved entries must be created after that date.
     * @param endingDate a string containing an {@code ISO-8601} date. Retrieved entries must be created before that date.
     * @param authentication the user authentication information.
     * @return a list of {@link EntryDTO} objects containing all the retrieved entries.
     */
    @GetMapping(path = "/dateRange")
    ResponseEntity<List<EntryDTO>> getEntriesByDateRange(@RequestParam("startingDate") @NotNull Instant startingDate, @RequestParam("endingDate") @NotNull Instant endingDate, Authentication authentication) {
        log.info("Called getEntriesByDateRange with startingDate: {} endingDate: {}", startingDate, endingDate);

        final List<EntryDTO> entries = entryService.getEntriesByDateRange(startingDate, endingDate, authentication.getName());

        return new ResponseEntity<>(entries,HttpStatus.OK);
    }

    /**
     * Returns the Map with the category UUID as the key and an Integer as value that corresponds to the price sum of
     * all entries that belong to that category and were created within the specified date range.
     *
     * @param categories the UUIDs of the categories.
     * @param startingDate a string containing an {@code ISO-8601} date. Retrieved entries must be created after that date to be included in the sum.
     * @param endingDate a string containing an {@code ISO-8601} date. Retrieved entries must be created before that date to be included in the sum.
     * @param authentication the user authentication information.
     * @return a {@link Map} with category {@link UUID} as the key and an {@link Integer} as the value.
     */
    @GetMapping(path = "/aggregates/by-category")
   ResponseEntity<Map<UUID, Integer>> getAggregatesByCategoryAndDateRange(
            @RequestParam("categories") @NotNull List<UUID> categories,
            @RequestParam("startingDate") @NotNull Instant startingDate,
            @RequestParam("endingDate") @NotNull Instant endingDate,
            Authentication authentication) {
        log.info("Called getAggregatesByCategoryAndDateRange with categories: {} startingDate: {} endingDate: {}", categories, startingDate, endingDate);

        final Map<UUID, Integer> result = this.entryService.getPriceSumOfCategoriesByDateRange(authentication.getName(),categories, startingDate, endingDate);

        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    /**
     * Returns the Map with the category UUID as the key and a list of Integers as value that corresponds to the price sum of
     * all entries that belong to that category and month.
     *
     * @param categories the UUIDs of the categories.
     * @param year a string containing the year of the entries i.e. 2024.
     * @param months al list of string containing the months of the entries i.e [01,02,03,04,11,12].
     * @param timezoneOffset the timezoneOffset of the client i.e +3:00 for GMT+3 or Z for UTC.
     * @param authentication the user authentication information.
     * @return a {@link Map} with category {@link UUID} as the key and an {@link Integer} as the value.
     */
    @GetMapping(path = "/aggregates/by-category-year-months")
    ResponseEntity<Map<UUID, List<Integer>>> getAggregatesByCategoryAndDYearForEachMonth(
            @RequestParam("categories") @NotNull List<UUID> categories,
            @RequestParam("year") @NotNull String year,
            @RequestParam("months") @NotNull List<String> months,
            @RequestParam("timezoneOffset") @NotNull String timezoneOffset,
            Authentication authentication) {
        log.info("Called getAggregatesByCategoryAndDYearForEachMonth with categories: {} year: {} timezoneOffset: {} months: {}", categories, year, timezoneOffset, months);

        final Map<UUID, List<Integer>> result = this.entryService.getAggregatesByCategoryAndDYearForEachMonth(authentication.getName(),categories, year, months, timezoneOffset);

        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    /**
     * Endpoint for creating the specified entries.
     * @param entries a list of {@link EntryDTO} objects containing all the data of the entries to be created.
     * @param authentication the user authentication information.
     * @return a {@link CreateEntriesResponse} containing the created entries as well as entries that failed to be created due to a conflict.
     */
    @PostMapping
    ResponseEntity<CreateEntriesResponse> createEntries(@Valid @RequestBody List<EntryDTO> entries, Authentication authentication) {
        log.info("Called createEntries with {}", entries);

        final CreateEntriesResponse response = entryService.saveEntries(entries,authentication.getName());
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
     * @param authentication the user authentication information.
     * @return a {@link UpdateEntriesResponse} containing the entries that were updated.
     * @throws CategoryNotFoundException if either the category to be replaced or the replacement category were not found.
     */
    @PutMapping(path = "/replaceCategory")
    ResponseEntity<UpdateEntriesResponse> replaceCategory(
            @RequestParam @NotBlank String oldCategoryUUID,
            @RequestParam @NotBlank String newCategoryUUID,
            Authentication authentication) throws CategoryNotFoundException {
        log.info("Called replaceCategory with oldCategoryUUID {} and newCategoryUUID {}", oldCategoryUUID, newCategoryUUID);

        final List<EntryDTO> updatedEntries = entryService.replaceCategory(oldCategoryUUID, newCategoryUUID, authentication.getName());

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
