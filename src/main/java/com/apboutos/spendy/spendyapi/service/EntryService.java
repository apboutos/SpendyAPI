package com.apboutos.spendy.spendyapi.service;

import com.apboutos.spendy.spendyapi.dto.EntryDTO;
import com.apboutos.spendy.spendyapi.exception.CategoryNotFoundException;
import com.apboutos.spendy.spendyapi.repository.CategoryRepository;
import com.apboutos.spendy.spendyapi.repository.EntryRepository;
import com.apboutos.spendy.spendyapi.response.entry.CreateEntriesResponse;
import com.apboutos.spendy.spendyapi.response.entry.DeleteEntriesResponse;
import com.apboutos.spendy.spendyapi.response.entry.UpdateEntriesResponse;
import com.apboutos.spendy.spendyapi.model.Category;
import com.apboutos.spendy.spendyapi.model.Entry;
import com.apboutos.spendy.spendyapi.model.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.sql.Timestamp.from;
import static java.time.Instant.now;

@Slf4j
@Service
@AllArgsConstructor
public class EntryService {

    private final EntryRepository entryRepository;
    private final UserService userService;
    private final CategoryRepository categoryRepository;


    public CreateEntriesResponse saveEntries(List<EntryDTO> entries, String username) throws IllegalArgumentException {

        final User user = userService.loadUserByUsername(username);
        final List<EntryDTO> savedEntries = new ArrayList<>();
        final List<EntryDTO> conflictingEntriesOnId = new ArrayList<>();
        final List<EntryDTO> conflictingEntriesOnCategory = new ArrayList<>();

        for (EntryDTO entry : entries) {

            final Optional<Entry> entrySearchResult = entryRepository.findEntryByUuid(entry.uuid());
            if (entrySearchResult.isPresent()) {
                conflictingEntriesOnId.add(entry);
            }
            else {
                final Optional<Category> categorySearchResult = categoryRepository.findCategoryByUuid(entry.category());
                if (categorySearchResult.isEmpty()) {
                    conflictingEntriesOnCategory.add(entry);
                }
                else {
                    final Entry newEntry = entryRepository.save(createEntryFromDTO(entry, user, categorySearchResult.get()));
                    savedEntries.add(createDTOFromEntry(newEntry));
                }
            }
        }
        return new CreateEntriesResponse(
                savedEntries,
                conflictingEntriesOnId,
                conflictingEntriesOnCategory);
    }

    public List<EntryDTO> getEntries(Instant lastPullRequestTimestamp, String username) {

        final User user = userService.loadUserByUsername(username);

        final List<Entry> entriesReturnedBySearch = entryRepository.findEntriesByUsernameAndLastUpdateAfter(user, lastPullRequestTimestamp);

        return entriesReturnedBySearch.stream().map(this::createDTOFromEntry).collect(Collectors.toList());
    }

    public List<EntryDTO> getEntriesByDateRange(Instant startingDate, Instant endingDate, String username) {

        final User user = userService.loadUserByUsername(username);

        final List<Entry> entriesReturnedBySearch = entryRepository.findEntryByUsernameAndCreatedAt(user, startingDate, endingDate);

        return entriesReturnedBySearch.stream().map(this::createDTOFromEntry).collect(Collectors.toList());

    }

    public Map<UUID, List<Integer>> getAggregatesByCategoryAndDYearForEachMonth(String username, List<UUID> categories, String year, List<String> months, String timezoneOffset) {

        final User user = userService.loadUserByUsername(username);
        final Map<UUID, List<Integer>> sumsPerCategory = new HashMap<>();

        for (UUID category : categories) {

            final List<Integer> sumsPerMonth = new ArrayList<>();

            for (String month: months) {

                final YearMonth yearMonth = YearMonth.of(Integer.parseInt(year), Integer.parseInt(month));

                final Instant startingDate = OffsetDateTime.parse(year + "-" + month + "-01T00:00:00" + timezoneOffset).toInstant();
                final Instant endingDate = OffsetDateTime.parse(yearMonth.atEndOfMonth() + "T23:59:59" + timezoneOffset).toInstant();
                log.info("StartingDate {}", startingDate);
                log.info("EndingDate {}", endingDate);

                final Integer sum = entryRepository.getSumOfPricesByUsernameAndCategoryAndDateRange(user, category, startingDate, endingDate);

                sumsPerMonth.add(sum);
            }

            sumsPerCategory.put(category, sumsPerMonth);
        }

        return  sumsPerCategory;
    }

    public Map<UUID, Integer> getPriceSumOfCategoriesByDateRange(String username, List<UUID> categories, Instant startingDate, Instant endingDate) {

        final User user = userService.loadUserByUsername(username);
        final Map<UUID, Integer> sumPerCategory = new HashMap<>();

        for (UUID categoryUUID : categories) {
            final Integer sum = this.entryRepository.getSumOfPricesByUsernameAndCategoryAndDateRange(user,categoryUUID, startingDate, endingDate);

            sumPerCategory.put(categoryUUID, sum);
        }

        return sumPerCategory;
    }

    public int countEntriesByCategory(Category category, String username) {
        final User user = userService.loadUserByUsername(username);
        return entryRepository.countEntriesByUsernameAndCategory(user, category);
    }

    @Transactional
    public DeleteEntriesResponse deleteEntries(List<UUID> entries) {

        final List<UUID> conflictingEntries = new ArrayList<>();
        entries.forEach(e -> {
            entryRepository.deleteEntryByUuid(e);
            if (entryRepository.findEntryByUuid(e).isPresent()) {
                conflictingEntries.add(e);
            }
        });

        if (conflictingEntries.isEmpty()) {
            return new DeleteEntriesResponse(HttpStatus.NO_CONTENT, true, "All entries have been deleted", from(now()), new ArrayList<>());
        }
        else {
            return new DeleteEntriesResponse(HttpStatus.CONFLICT, false, "Some entries could not be deleted", from(now()), conflictingEntries);
        }
    }

    @Transactional
    public void deleteEntriesByCategory(String categoryUUID) throws CategoryNotFoundException {

        final Optional<Category> category = this.categoryRepository.findCategoryByUuid(UUID.fromString(categoryUUID));
        if (category.isEmpty()) {
            throw new CategoryNotFoundException("The category whose entries you wanted to delete does not exist.");
        }
        else {
            this.entryRepository.deleteByCategory(category.get());
        }
    }

    @Transactional
    public List<EntryDTO> replaceCategory(String oldCategoryUUID, String newCategoryUUID, String username) throws CategoryNotFoundException {

        final User user = userService.loadUserByUsername(username);
        final Optional<Category> oldCategory = this.categoryRepository.findCategoryByUuid(UUID.fromString(oldCategoryUUID));
        final Optional<Category> newCategory = this.categoryRepository.findCategoryByUuid(UUID.fromString(newCategoryUUID));
        final List<EntryDTO> updatedEntries = new ArrayList<>();

        if (oldCategory.isEmpty()) {
            throw new CategoryNotFoundException("The category you wanted to replace does not exist");
        }
        else  if (newCategory.isEmpty()) {
            throw new CategoryNotFoundException("The replacement category does not exist");
        }
        else {
            final List<Entry> entriesToBeModified = this.entryRepository.findEntriesByUsernameAndCategory(user,oldCategory.get());
            for (Entry entry: entriesToBeModified) {
                final Instant lastUpdate = Instant.now();

                this.entryRepository.updateEntry(
                        entry.getUuid(),
                        entry.getType(),
                        entry.getDescription(),
                        entry.getPrice(),
                        newCategory.get(),
                        lastUpdate,
                        entry.getIsDeleted());

                updatedEntries.add(new EntryDTO(
                        entry.getUuid(),
                        entry.getType(),
                        newCategory.get().getUuid(),
                        entry.getDescription(),
                        entry.getPrice(),
                        entry.getCreatedAt(),
                        lastUpdate,
                        entry.getIsDeleted()));
            }
        }
        return updatedEntries;
    }

    @Transactional
    public UpdateEntriesResponse updateEntries(List<EntryDTO> entries) {

        final List<EntryDTO> updatedEntries = new ArrayList<>();
        final List<EntryDTO> conflictingEntriesOnId = new ArrayList<>();
        final List<EntryDTO> conflictingEntriesOnCategory = new ArrayList<>();
        final List<EntryDTO> conflictingEntriesOnLastUpdate = new ArrayList<>();

        for (EntryDTO entry : entries) {

            final Optional<Entry> result = entryRepository.findEntryByUuid(entry.uuid());
            final Optional<Category> categorySearchResult = categoryRepository.findCategoryByUuid(entry.category());

            if (result.isEmpty()) {
                conflictingEntriesOnId.add(entry);
            }
            else if (categorySearchResult.isEmpty()) {
                conflictingEntriesOnCategory.add(entry);
            }
            else if (result.get().getLastUpdate().isAfter(entry.lastUpdate())) {
                conflictingEntriesOnLastUpdate.add(entry);
            }
            else {
                entryRepository.updateEntry(
                        entry.uuid(),
                        entry.type(),
                        entry.description(),
                        entry.price(),
                        categorySearchResult.get(),
                        entry.lastUpdate(),
                        entry.isDeleted());
                updatedEntries.add(entry);
            }
        }

        return new UpdateEntriesResponse(
                updatedEntries,
                conflictingEntriesOnId,
                conflictingEntriesOnCategory,
                conflictingEntriesOnLastUpdate);
    }

    private Entry createEntryFromDTO(EntryDTO entryDTO, User user, Category category) {
        return new Entry(
                entryDTO.uuid(),
                user, 
                entryDTO.type(),
                category,
                entryDTO.description(),
                entryDTO.price(),
                entryDTO.date(),
                entryDTO.lastUpdate(),
                entryDTO.isDeleted());
    }

    private EntryDTO createDTOFromEntry(Entry entry) {
        return new EntryDTO(
                entry.getUuid(),
                entry.getType(),
                entry.getCategory().getUuid(),
                entry.getDescription(),
                entry.getPrice(),
                entry.getCreatedAt(),
                entry.getLastUpdate(),
                entry.getIsDeleted());

    }


}

