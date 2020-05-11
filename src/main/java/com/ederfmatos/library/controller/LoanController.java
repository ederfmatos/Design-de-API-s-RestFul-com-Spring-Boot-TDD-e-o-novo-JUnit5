package com.ederfmatos.library.controller;

import com.ederfmatos.library.bean.loan.LoanDTO;
import com.ederfmatos.library.model.Book;
import com.ederfmatos.library.model.Loan;
import com.ederfmatos.library.service.BookService;
import com.ederfmatos.library.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

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
        Book book = bookService.getBookByIsbn(loanDTO.getIsbn()).get();

        Loan loan = Loan.builder()
                .book(book)
                .customer(loanDTO.getCustomer())
                .timestamp(LocalDateTime.now())
                .build();

        loan = loanService.save(loan);
        System.out.println(loan);
        return loan.getId();
    }


}
