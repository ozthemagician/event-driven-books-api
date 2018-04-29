package com.example.booksapi.persistence;


import com.example.booksapi.domain.Book;

import java.util.UUID;

public interface BookRepository {

    void save(Book book);

    Book find(UUID id);
}
