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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EntryControllerTest {

    @InjectMocks EntryController classUnderTest;

    @Mock
    Authentication authenticationMock;

    @Mock
    EntryService entryServiceMock;

    @Test
    void getAggregatesByCategory() {

        final String date = "2024-03-01T16:44:37.113Z";
        final UUID category = UUID.fromString("a704a3ca-22eb-430c-8895-8c3ae848b7e9");
        final List<UUID> categories = List.of(category);
        final String username = "username";

        final Map<UUID, List<Integer>> sumOfEntries = Map.of(category, List.of(0, 100, 1000, 10000));

        Mockito.when(authenticationMock.getName())
                .thenReturn(username);
        Mockito.when(entryServiceMock.getPriceSumByDate(username, categories, 1, 3, 2024))
                .thenReturn(sumOfEntries);

        final ResponseEntity<Map<UUID, List<Integer>>> result = classUnderTest.getAggregatesByCategory(categories, date, authenticationMock);

        Assertions.assertEquals(result.getStatusCode(), HttpStatus.OK);
        Assertions.assertEquals(result.getBody(), sumOfEntries);

    }
}