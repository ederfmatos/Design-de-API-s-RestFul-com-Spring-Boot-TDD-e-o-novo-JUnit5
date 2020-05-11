package com.ederfmatos.library.service.impl;

import com.ederfmatos.library.exception.BusinessException;
import com.ederfmatos.library.model.Book;
import com.ederfmatos.library.repository.BookRepository;
import com.ederfmatos.library.service.BookService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.springframework.data.domain.ExampleMatcher.StringMatcher.CONTAINING;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book entity) {
        if(repository.existsByIsbn(entity.getIsbn())) {
            throw new BusinessException("Isbn já cadastrado");
        }
        return repository.save(entity);
    }

    @Override
    public Optional<Book> getById(long id) {
        return repository.findById(id);
    }

    @Override
    public void deleteById(Book book) {
        if(book == null) {
            throw new IllegalArgumentException("Book is required");
        }

        repository.delete(book);
    }

    @Override
    public void update(Book book) {
        if(book == null) {
            throw new IllegalArgumentException("Book is required");
        }

        repository.save(book);
    }

    @Override
    public Page<Book> find(Book filter, Pageable pageRequest) {
        Example<Book> example = Example.of(filter, ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withIgnoreNullValues()
                .withStringMatcher(CONTAINING)
        );

        return repository.findAll(example, pageRequest);
    }

    @Override
    public Optional<Book> getBookByIsbn(String isbn) {
        return Optional.empty();
    }

}
