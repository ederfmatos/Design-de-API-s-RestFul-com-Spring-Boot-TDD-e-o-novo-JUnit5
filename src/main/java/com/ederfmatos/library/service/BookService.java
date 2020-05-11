package com.ederfmatos.library.service;

import com.ederfmatos.library.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface BookService {

    Book save(Book any);

    Optional<Book> getById(long id);

    void deleteById(Book book);

    void update(Book book);

    Page<Book> find(Book filter, Pageable pageRequest);

}
