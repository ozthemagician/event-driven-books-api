package com.example.booksapi.domain.event;

import java.time.Instant;

public interface DomainEvent {

    Instant occuredAt();
}
