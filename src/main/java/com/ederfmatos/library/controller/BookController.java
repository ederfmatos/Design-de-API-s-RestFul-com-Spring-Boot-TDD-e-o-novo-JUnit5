package com.ederfmatos.library.controller;

import com.ederfmatos.library.bean.BookPersistBean;
import com.ederfmatos.library.model.Book;
import com.ederfmatos.library.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookPersistBean create(@RequestBody BookPersistBean in) {
        Book entity = Book.builder().author(in.getAuthor()).title(in.getTitle()).isbn(in.getIsbn()).build();
        entity = service.save(entity);

        return BookPersistBean.builder().id(entity.getId()).author(in.getAuthor()).title(in.getTitle()).isbn(in.getIsbn()).build();
    }

}
