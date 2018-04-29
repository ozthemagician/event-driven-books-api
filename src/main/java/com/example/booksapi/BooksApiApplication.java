package com.example.booksapi;

import com.example.booksapi.domain.Book;
import com.example.booksapi.persistence.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.UUID;

@EnableScheduling
@EnableBinding(Source.class)
@SpringBootApplication
@RequiredArgsConstructor
public class BooksApiApplication {

    private final BookRepository bookRepository;

    public static void main(String[] args) {
        SpringApplication.run(BooksApiApplication.class, args);
    }

    @Scheduled(fixedRate = 2000L)
    public void randomBooks() {
        Book book = Book.builder().id(UUID.randomUUID()).title("For whom the bell tools").build();
        book.changeAuthorTo("Charles Dickens");
        book.release();
        bookRepository.save(book);
        book.withdraw();
        bookRepository.save(book);
    }
}
