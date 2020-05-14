package com.ederfmatos.library.controller;

import com.ederfmatos.library.bean.book.BookGetBean;
import com.ederfmatos.library.bean.book.BookPersistBean;
import com.ederfmatos.library.bean.book.BookUpdateBean;
import com.ederfmatos.library.bean.loan.LoanDTO;
import com.ederfmatos.library.model.Book;
import com.ederfmatos.library.model.Loan;
import com.ederfmatos.library.service.BookService;
import com.ederfmatos.library.service.LoanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

import static com.ederfmatos.library.lib.LibraryMapper.getMapper;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/books")
@Api("Book API")
@Slf4j
public class BookController {

    @Autowired
    private BookService service;

    @Autowired
    private LoanService loanService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("CREATE A BOOK")
    public BookPersistBean create(@RequestBody @Valid BookPersistBean in) {
        log.info("Create a book for isbn: {}", in.getIsbn());

        Book entity = getMapper().map(in, Book.class);
        entity = service.save(entity);

        return getMapper().map(entity, BookPersistBean.class);
    }

    @GetMapping("/{id}")
    @ApiOperation("FIND A BOOK DETAILS BY ID")
    public BookGetBean findById(@PathVariable long id) {
        log.info("Obtaining id for book id: {}", id);

        return service
                .getById(id)
                .map(book -> getMapper().map(book, BookGetBean.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{id}/loans")
    @ApiOperation("FIND LOANS")
    public Page<LoanDTO> findLoansByBook(@PathVariable long id, Pageable pageable) {
        return service.getById(id).map(book -> {
            Page<Loan> page = loanService.getLoanByBook(book, pageable);
            List<LoanDTO> loans = page.getContent().stream().map(loan -> getMapper().map(loan, LoanDTO.class)).collect(toList());
            return new PageImpl<>(loans, pageable, page.getTotalElements());
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    @ApiOperation("FIND BOOK BY PARAMS")
    public Page<BookGetBean> find(BookGetBean book, Pageable pageRequest) {
        Book filter = getMapper().map(book, Book.class);
        Page<Book> result = service.find(filter, pageRequest);

        List<BookGetBean> books = result.getContent().stream().map(entity -> getMapper().map(entity, BookGetBean.class)).collect(toList());

        return new PageImpl<>(books, pageRequest, result.getTotalElements());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation("DELETE A BOOK BY ID")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Book successfully deleted")
    })
    public void deleteById(@PathVariable long id) {
        log.info("Deleting book of id: {}", id);
        Book book = service.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        service.deleteById(book);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("UPDATE A BOOK")
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

}
