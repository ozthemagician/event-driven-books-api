package com.example.booksapi.persistence;

import com.example.booksapi.domain.Book;
import com.example.booksapi.domain.event.DomainEvent;
import com.example.booksapi.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class EventSourcedBookRepository implements BookRepository {


    private final EventPublisher eventPublisher;
    private final Map<UUID, List<DomainEvent>> events = new ConcurrentHashMap<>();

    @Override
    public void save(Book book) {
        List<DomainEvent> newChanges = book.getChanges();
        List<DomainEvent> currentChanges = events.getOrDefault(book.getId(), new ArrayList<>());
        currentChanges.addAll(newChanges);
        events.put(book.getId(), currentChanges);
        book.flushChanges();
        newChanges.forEach(eventPublisher::sendEvent);
    }

    @Override
    public Book find(UUID id) {
        if (!events.containsKey(id)) {
            return null;
        }
        return Book.recreateFrom(id, events.get(id));
    }

    public Book find(UUID id, Instant timestamp) {
        if (!events.containsKey(id)) {
            return null;
        }
        List<DomainEvent> domainEvents = events.get(id)
                .stream()
                .filter(event -> !event.occuredAt().isAfter(timestamp))
                .collect(Collectors.toList());
        return Book.recreateFrom(id, domainEvents);
    }
}
