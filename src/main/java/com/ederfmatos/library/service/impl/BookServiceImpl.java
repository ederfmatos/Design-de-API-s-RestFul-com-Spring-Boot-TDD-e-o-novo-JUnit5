package com.ederfmatos.library.service.impl;

import com.ederfmatos.library.model.Book;
import com.ederfmatos.library.repository.BookRepository;
import com.ederfmatos.library.service.BookService;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book entity) {
        return repository.save(entity);
    }

}
