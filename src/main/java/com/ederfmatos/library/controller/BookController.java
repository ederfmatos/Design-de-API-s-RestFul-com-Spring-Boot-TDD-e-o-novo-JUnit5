package com.ederfmatos.library.controller;

import com.ederfmatos.library.bean.BookPersistBean;
import com.ederfmatos.library.exception.BusinessException;
import com.ederfmatos.library.lib.bean.ApiErrors;
import com.ederfmatos.library.model.Book;
import com.ederfmatos.library.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;

import static com.ederfmatos.library.lib.LibraryMapper.getMapper;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookPersistBean create(@RequestBody @Valid BookPersistBean in) {
        Book entity = getMapper().map(in, Book.class);
        entity = service.save(entity);

        return getMapper().map(entity, BookPersistBean.class);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidationExceptions(MethodArgumentNotValidException exception) {
        BindingResult bindingResult = exception.getBindingResult();
        return new ApiErrors(bindingResult);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleBusinessException(BusinessException exception) {
        return new ApiErrors(exception);
    }

}
