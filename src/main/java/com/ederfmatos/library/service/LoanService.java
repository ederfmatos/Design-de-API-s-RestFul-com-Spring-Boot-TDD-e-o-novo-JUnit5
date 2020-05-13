package com.ederfmatos.library.service;

import com.ederfmatos.library.bean.loan.LoanFilterDTO;
import com.ederfmatos.library.model.Book;
import com.ederfmatos.library.model.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface LoanService {

    Loan save(Loan loan);

    Optional<Loan> getById(long id);

    Loan update(Loan loan);

    Page<Loan> find(LoanFilterDTO loan, Pageable pageable);

    Page<Loan> getLoanByBook(Book book, Pageable pageable);

}
