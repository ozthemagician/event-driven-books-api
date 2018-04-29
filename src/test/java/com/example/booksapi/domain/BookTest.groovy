package com.example.booksapi.domain

import spock.lang.Specification

class BookTest extends Specification {

    Book book = Book.builder().id(UUID.randomUUID()).build()

    def 'discontinued book cannot be released'() {
        given:
        book.setState(Book.BookState.DISCONTINUED)
        when:
        book.release()
        then:
        thrown(IllegalStateException)

    }

    def 'on sale book cannot be released'() {
        given:
        book.setState(Book.BookState.ON_SALE)
        when:
        book.release()
        then:
        thrown(IllegalStateException)
    }

    def 'discontinued book cannot be on sale'() {
        given:
        book.withdraw()
        when:
        book.isOnSale()
        then:
        thrown(IllegalStateException)
    }

    def 'new book can be released'() {
        when:
        !book.isOnSale() | !book.isDiscontinued()
        then:
        book.release()
    }

    def 'discontinued book cannot change author'() {
        given:
        book.setState(Book.BookState.DISCONTINUED)
        when:
        book.changeAuthorTo("Some Name")
        then:
        thrown(IllegalStateException)
    }

    def 'on sale book cannot change author'() {
        given:
        book.setState(Book.BookState.ON_SALE)
        when:
        book.changeAuthorTo("some name")
        then:
        thrown(IllegalStateException)
    }
}
