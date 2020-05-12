package com.ederfmatos.library.service;

import com.ederfmatos.library.model.Loan;
import com.ederfmatos.library.repository.LoanRepository;

public class LoanServiceImpl implements LoanService {

    private final LoanRepository repository;

    public LoanServiceImpl(LoanRepository repository) {
        this.repository = repository;
    }

    @Override
    public Loan save(Loan loan) {
        return repository.save(loan);
    }

}
