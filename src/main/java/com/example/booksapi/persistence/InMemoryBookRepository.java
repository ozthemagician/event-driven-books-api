package com.example.booksapi.persistence;

import com.example.booksapi.domain.Book;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryBookRepository implements BookRepository {

    private final Map<UUID, Book> books = new ConcurrentHashMap<>();


    @Override
    public void save(Book book) {
        books.put(book.getId(), book);
    }

    @Override
    public Book find(UUID id) {
        return books.get(id);
    }
}
