package com.ederfmatos.library.controller;

import com.ederfmatos.library.bean.BookGetBean;
import com.ederfmatos.library.bean.BookPersistBean;
import com.ederfmatos.library.bean.BookUpdateBean;
import com.ederfmatos.library.exception.BusinessException;
import com.ederfmatos.library.lib.bean.ApiErrors;
import com.ederfmatos.library.model.Book;
import com.ederfmatos.library.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

import static com.ederfmatos.library.lib.LibraryMapper.getMapper;
import static java.util.stream.Collectors.toList;

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

    @GetMapping("/{id}")
    public BookGetBean findById(@PathVariable long id) {
        return service
                .getById(id)
                .map(book -> getMapper().map(book, BookGetBean.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public Page<BookGetBean> find(BookGetBean book, Pageable pageRequest) {
        Book filter = getMapper().map(book, Book.class);
        Page<Book> result = service.find(filter, pageRequest);

        List<BookGetBean> books = result.getContent().stream().map(entity -> getMapper().map(entity, BookGetBean.class)).collect(toList());

        return new PageImpl<>(books, pageRequest, result.getTotalElements());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable long id) {
        Book book = service.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        service.deleteById(book);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookGetBean update(@PathVariable long id, @RequestBody BookUpdateBean bean) {
        return service
                .getById(id)
                .map(book -> {
                    bean.setId(id);
                    book = getMapper().map(bean, Book.class);
                    service.update(book);
                    return getMapper().map(book, BookGetBean.class);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
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
