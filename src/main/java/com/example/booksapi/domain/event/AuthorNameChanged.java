package com.example.booksapi.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@AllArgsConstructor
@Getter
public class AuthorNameChanged implements DomainEvent {

    private String authorName;
    private final Instant when;

    @Override
    public Instant occuredAt() {
        return when;
    }
}
