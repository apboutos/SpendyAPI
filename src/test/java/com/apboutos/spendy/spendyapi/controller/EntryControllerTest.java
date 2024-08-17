package com.apboutos.spendy.spendyapi.controller;

import com.apboutos.spendy.spendyapi.service.EntryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class EntryControllerTest {

    @InjectMocks EntryController classUnderTest;

    @Mock
    Authentication authenticationMock;

    @Mock
    EntryService entryServiceMock;

    @Test
    void getAggregatesByCategory() {

        final String startingDate = "2024-03-01T16:44:37.113Z";
        final String endingDate = "2024-03-02T16:44:37.113Z";
        final UUID category = UUID.fromString("a704a3ca-22eb-430c-8895-8c3ae848b7e9");
        final List<UUID> categories = List.of(category);
        final String username = "username";

        final Map<UUID, Integer> sumOfEntries = Map.of(category, 1000);

        Mockito.when(authenticationMock.getName())
                .thenReturn(username);
        Mockito.when(entryServiceMock.getPriceSumOfCategoriesByDateRange(username, categories, Instant.parse(
                startingDate), Instant.parse(endingDate)))
                .thenReturn(sumOfEntries);

        final ResponseEntity<Map<UUID, Integer>> result = classUnderTest.getAggregatesByCategoryAndDateRange(
                categories, Instant.parse(startingDate), Instant.parse(endingDate), authenticationMock);

        Assertions.assertEquals(result.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(result.getBody(), sumOfEntries);

    }
}