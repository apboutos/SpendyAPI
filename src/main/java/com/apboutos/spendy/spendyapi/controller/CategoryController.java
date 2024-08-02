package com.apboutos.spendy.spendyapi.controller;

import com.apboutos.spendy.spendyapi.dto.CategoryDTO;
import com.apboutos.spendy.spendyapi.exception.CategoryExistsException;
import com.apboutos.spendy.spendyapi.exception.CategoryHasEntriesException;
import com.apboutos.spendy.spendyapi.exception.CategoryNotFoundException;
import com.apboutos.spendy.spendyapi.service.CategoryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@Slf4j
@RestController
@RequestMapping(value = "/api/v1/categories",produces = "application/json")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Endpoint for the retrieval of all categories of a certain user.
     *
     * @param authentication the user authentication information.
     * @return a list of {@link CategoryDTO} object containing all the retrieved categories.
     */
    @GetMapping
    public List<CategoryDTO> getAllCategories(Authentication authentication) {
        log.info("Called getAllCategories");

        return categoryService.getAllCategoriesOfUser(authentication.getName());
    }

    /**
     * Endpoint for the creation of a category.
     *
     * @param categoryDTO a {@link CategoryDTO} containing all the data of the category to be created.
     * @param authentication the user authentication information.
     * @return a {@link CategoryDTO} containing the created category.
     * @throws CategoryExistsException if there was a conflict during the creation of the category.
     */
    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO, Authentication authentication) throws CategoryExistsException {
        log.info("Called createCategory with {}",categoryDTO);

        final CategoryDTO createdCategory = categoryService.createCategory(categoryDTO, authentication.getName());
        return new ResponseEntity<>(createdCategory,HttpStatus.CREATED);
    }

    /**
     * Endpoint for the update of a category.
     *
     * @param categoryDTO a {@link CategoryDTO} containing all the data of the category to be updated.
     * @return a {@link CategoryDTO} containing the updated category.
     * @throws CategoryNotFoundException if the specified category was not found.
     */
    @PutMapping
    public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO) throws CategoryNotFoundException {
        log.info("Called updateCategory with {}",categoryDTO);

        final CategoryDTO updatedCategory = categoryService.updateCategory(categoryDTO);
        return new ResponseEntity<>(updatedCategory,HttpStatus.OK);
    }

    /**
     * Endpoint for deleting a category.
     * @param categoryUUID the {@link UUID} of the category to be deleted.
     * @param authentication the user authentication information.
     * @return a {@link ResponseEntity} containing the status {@code 204 (NO_CONTENT)} if the category was deleted.
     * @throws CategoryHasEntriesException if the category has entries and therefore cannot be deleted.
     */
    @DeleteMapping
    public ResponseEntity<Object> deleteCategory(@RequestParam String categoryUUID, Authentication authentication) throws CategoryHasEntriesException {
        log.info("Called deleteCategory with UUID {}", categoryUUID);

        categoryService.deleteCategory(categoryUUID, authentication.getName());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }
}
