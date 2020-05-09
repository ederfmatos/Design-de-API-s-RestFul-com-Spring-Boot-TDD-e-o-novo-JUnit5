package com.ederfmatos.library.builder;

import com.ederfmatos.library.model.Book;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    public BookBuilder withId(long id) {
        book.setId(id);
        return this;
    }

    public BookBuilder withIsbn(String isbn) {
        book.setIsbn(isbn);
        return this;
    }

    public BookBuilder withTitle(String title) {
        book.setTitle(title);
        return this;
    }

    public BookBuilder withAuthor(String author) {
        book.setAuthor(author);
        return this;
    }

    public Book build() {
        return book;
    }

    public String inJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(book);
    }
}
