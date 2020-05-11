package com.ederfmatos.library.service;

import com.ederfmatos.library.model.Loan;
import org.springframework.stereotype.Service;

@Service
public interface LoanService {

    Loan save(Loan loan);

}
