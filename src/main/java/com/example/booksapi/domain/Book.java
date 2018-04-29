package com.example.booksapi.domain;

import com.google.common.collect.ImmutableList;
import com.example.booksapi.domain.event.AuthorNameChanged;
import com.example.booksapi.domain.event.BookIsDiscontinued;
import com.example.booksapi.domain.event.BookIsReleased;
import com.example.booksapi.domain.event.DomainEvent;
import io.vavr.API;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.Predicates.instanceOf;
import static io.vavr.collection.Stream.ofAll;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class Book {


    public enum BookState {
        NEW, ON_SALE, DISCONTINUED
    }

    private final UUID id;

    private final String title;

    private String authorName;
    private BookState state = BookState.NEW;

    private final List<DomainEvent> changes = new ArrayList<>();

    public List<DomainEvent> getChanges() {
        return ImmutableList.copyOf(changes);
    }

    public void flushChanges() {
        changes.clear();
    }

    public static Book recreateFrom(UUID id, List<DomainEvent> domainEvents) {
        return ofAll(domainEvents).foldLeft(Book.builder().id(id).build(), Book::handleEvent);
    }

    private Book handleEvent(DomainEvent d) {
        return API.Match(d).of(
                Case($(instanceOf(BookIsDiscontinued.class)), this::bookIsDiscontinued),
                Case($(instanceOf(BookIsReleased.class)), this::bookIsReleased),
                Case($(instanceOf(AuthorNameChanged.class)), this::authorNameChanged)
        );
    }

    public void changeAuthorTo(String authorName) {
        if (this.isDiscontinued()) {
            throw new IllegalStateException("Cannot change aouthor. Book is discontinued.");
        }
        if (this.isOnSale()) {
            throw new IllegalStateException("Cannot change author. Book is on sale.");
        }
        authorNameChanged(new AuthorNameChanged(authorName, Instant.now()));
    }

    // The State change is the name of EVENT itself
    private Book authorNameChanged(AuthorNameChanged event) {
        this.authorName = event.getAuthorName();
        changes.add(event);
        return this;
    }

    public void release() {
        if (this.isDiscontinued() || this.isOnSale()) {
            throw new IllegalStateException("This book is discontinued or is on sale");
        }
        bookIsReleased(new BookIsReleased(Instant.now()));
    }

    private Book bookIsReleased(BookIsReleased event) {
        state = BookState.ON_SALE;
        changes.add(event);
        return this;
    }

    public void withdraw() {
        bookIsDiscontinued(new BookIsDiscontinued(Instant.now()));
    }

    private Book bookIsDiscontinued(BookIsDiscontinued bookIsDiscontinued) {
        state = BookState.DISCONTINUED;
        changes.add(bookIsDiscontinued);
        return this;
    }

    public boolean isDiscontinued() {
        return this.state == BookState.DISCONTINUED;
    }

    public boolean isOnSale() {
        if (this.isDiscontinued()) {
            throw new IllegalStateException("This book is discontinued.");
        }
        return this.state == BookState.ON_SALE;
    }

    public String getBookName() {
        return title;
    }
}
