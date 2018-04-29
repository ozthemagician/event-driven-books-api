package com.example.booksapi.persistence

import com.example.booksapi.publisher.EventPublisher
import spock.lang.Specification

import java.time.Instant

class BookRepositoryTest extends Specification {

    EventPublisher eventPublisher = new EventPublisher()

    EventSourcedBookRepository bookRepository = new EventSourcedBookRepository(eventPublisher)

    def 'shoud be able to save and load book'() {
        given:
        UUID id = UUID.randomUUID()
        and:
        com.example.booksapi.domain.Book book = com.example.booksapi.domain.Book.builder().id(id).build()
        and:
        book.setState(com.example.booksapi.domain.Book.BookState.NEW)
        and:
        book.changeAuthorTo("Charles Dickens")
        when:
        bookRepository.save(book)
        and:
        com.example.booksapi.domain.Book bookSaved = bookRepository.find(id)
        then:
        !bookSaved.isOnSale()
        !bookSaved.isDiscontinued()
        bookSaved.getAuthorName() == "Charles Dickens"
    }

    def 'shoud be able to load state from a historic timestamp'() {
        given:
        UUID id = UUID.randomUUID()
        and:
        com.example.booksapi.domain.Book book = com.example.booksapi.domain.Book.builder().id(id).build()
        and:
        book.setState(com.example.booksapi.domain.Book.BookState.NEW)
        and:
        book.changeAuthorTo("Charles Dickens")
        when:
        bookRepository.save(book)
        and:
        sleep(1000)
        and:
        book.changeAuthorTo("Edgar Alan Poe")
        and:
        bookRepository.save(book)
        then:
        bookRepository.find(id).getAuthorName() == "Edgar Alan Poe"
        bookRepository.find(id, Instant.now().minusMillis(1005)).getAuthorName() == "Charles Dickens"
    }
}
