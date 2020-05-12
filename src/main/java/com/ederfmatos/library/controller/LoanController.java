package com.ederfmatos.library.controller;

import com.ederfmatos.library.bean.loan.LoanDTO;
import com.ederfmatos.library.model.Loan;
import com.ederfmatos.library.service.BookService;
import com.ederfmatos.library.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RestController
@RequestMapping("/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @Autowired
    private BookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public long create(@RequestBody LoanDTO loanDTO) {
        return bookService.getBookByIsbn(loanDTO.getIsbn())
                .map(book -> {
                    Loan loan = Loan.builder()
                            .book(book)
                            .customer(loanDTO.getCustomer())
                            .timestamp(LocalDate.now())
                            .build();

                    loan = loanService.save(loan);
                    return loan.getId();
                }).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for this isbn"));
    }


}
