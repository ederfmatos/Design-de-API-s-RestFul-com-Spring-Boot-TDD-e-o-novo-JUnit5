package com.ederfmatos.library.service;

import com.ederfmatos.library.model.Book;
import org.springframework.stereotype.Service;

@Service
public interface BookService {

    Book save(Book any);

}
