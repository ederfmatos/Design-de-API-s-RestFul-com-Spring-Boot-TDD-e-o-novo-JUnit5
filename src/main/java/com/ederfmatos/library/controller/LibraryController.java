package com.ederfmatos.library.controller;

import com.ederfmatos.library.bean.BookPersistBean;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
public class LibraryController {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookPersistBean create(@RequestBody BookPersistBean in) {
        in.setId(5);
        return in;
    }

}
