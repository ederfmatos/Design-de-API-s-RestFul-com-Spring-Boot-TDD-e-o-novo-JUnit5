package com.ederfmatos.library.service;

import com.ederfmatos.library.model.Loan;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface LoanService {

    Loan save(Loan loan);

    Optional<Loan> findById(long id);

    void update(Loan loan);

}
