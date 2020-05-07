package com.ederfmatos.library.builder;

import com.ederfmatos.library.model.Book;

public class BookBuilder {

    private BookBuilder() {
        setDefaultAttributes();
    }

    private Book book;

    private void setDefaultAttributes() {
        book = Book.builder().title("title").author("Author").isbn("123123").build();
    }

    public static BookBuilder oneBook() {
        return new BookBuilder();
    }

    public BookBuilder withId(int id) {
        book.setId(id);
        return this;
    }

    public BookBuilder withIsbn(String isbn) {
        book.setIsbn(isbn);
        return this;
    }

    public Book build() {
        return book;
    }
}
