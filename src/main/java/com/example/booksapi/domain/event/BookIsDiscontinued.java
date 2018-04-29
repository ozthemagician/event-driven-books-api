package com.example.booksapi.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@AllArgsConstructor
@Getter
public class BookIsDiscontinued implements DomainEvent {

    private Instant discontinuedAt;

    @Override
    public Instant occuredAt() {
        return discontinuedAt;
    }
}
