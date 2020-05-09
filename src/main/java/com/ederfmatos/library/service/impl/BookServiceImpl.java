package com.ederfmatos.library.service.impl;

import com.ederfmatos.library.exception.BusinessException;
import com.ederfmatos.library.model.Book;
import com.ederfmatos.library.repository.BookRepository;
import com.ederfmatos.library.service.BookService;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    }

    @Override
    public void update(Book book) {

    }

}
