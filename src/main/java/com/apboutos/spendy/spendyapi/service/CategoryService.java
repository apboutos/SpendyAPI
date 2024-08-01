package com.apboutos.spendy.spendyapi.service;

import com.apboutos.spendy.spendyapi.dto.CategoryDTO;
import com.apboutos.spendy.spendyapi.exception.CategoryExistsException;
import com.apboutos.spendy.spendyapi.exception.CategoryHasEntriesException;
import com.apboutos.spendy.spendyapi.exception.CategoryNotFoundException;
import com.apboutos.spendy.spendyapi.exception.UsernameNotFoundException;
import com.apboutos.spendy.spendyapi.model.Category;
import com.apboutos.spendy.spendyapi.model.User;
import com.apboutos.spendy.spendyapi.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;
    private final EntryService entryService;

    private final UserService userService;

    public List<CategoryDTO> getAllCategoriesOfUser(String username ) throws UsernameNotFoundException {

        final List<Category> categories = repository.findCategoriesByUser((User)userService.loadUserByUsername(username));

        return categories.stream()
                .map((category -> new CategoryDTO(
                        category.getUuid(),
                        category.getName(),
                        category.getType(),
                        category.getCreatedAt(),
                        category.getLastUpdate(),
                        category.getIsDeleted())))
                .collect(Collectors.toList());

    }

    public CategoryDTO createCategory(CategoryDTO categoryDTO, String username) throws CategoryExistsException, UsernameNotFoundException {

        final User user = (User) userService.loadUserByUsername(username);
        final Category category = new Category(
                categoryDTO.uuid(),
                categoryDTO.name(),
                categoryDTO.type(),
                user,
                categoryDTO.date(),
                categoryDTO.lastUpdate(),
                categoryDTO.isDeleted());

        final Optional<Category> searchResultByNameAndUser = repository.findCategoryByTypeAndNameAndUser(
                category.getType(),
                category.getName(),
                category.getUser());

        if (searchResultByNameAndUser.isPresent()) {
            throw new CategoryExistsException("This category already exists.");
        }

        final Optional<Category> searchResultByUUID = repository.findCategoryByUuid(category.getUuid());

        if (searchResultByUUID.isPresent()) {
            throw new CategoryExistsException("The UUID of this category already exists.");
        }

        final Category result;
        try {
            result = repository.saveAndFlush(category);
        }
        catch (Exception e) {
            throw new CategoryExistsException("This category already exists.");
        }

        return new CategoryDTO(
                result.getUuid(),
                result.getName(),
                result.getType(),
                result.getCreatedAt(),
                result.getLastUpdate(),
                result.getIsDeleted());
    }

    @Transactional
    public CategoryDTO updateCategory(CategoryDTO categoryDTO) throws CategoryNotFoundException {

        final Optional<Category> searchResult = repository.findCategoryByUuid(categoryDTO.uuid());
        if (searchResult.isEmpty()) {
            throw new CategoryNotFoundException("This category does not exist.");
        }
        final Timestamp lastUpdate = Timestamp.from(Instant.now());
        repository.updateCategory(
                categoryDTO.uuid(),
                categoryDTO.name(),
                categoryDTO.type(),
                lastUpdate,
                categoryDTO.isDeleted());

        return new CategoryDTO(
                categoryDTO.uuid(),
                categoryDTO.name(),
                categoryDTO.type(),
                categoryDTO.date(),
                lastUpdate,
                categoryDTO.isDeleted());
    }

    public void deleteCategory(String categoryUUID, String username) throws CategoryHasEntriesException, UsernameNotFoundException {

        final Optional<Category> searchResult = repository.findCategoryByUuid(UUID.fromString(categoryUUID));
        if (searchResult.isPresent()) {
            final Category  category = searchResult.get();
            final int numberOfEntriesInThisCategory = this.entryService.countEntriesByCategory(category, username);

            if (numberOfEntriesInThisCategory > 0) {
                throw new CategoryHasEntriesException("This category cannot be deleted as there are entries that belong to it");
            }
            this.repository.delete(category);
        }
    }
}
