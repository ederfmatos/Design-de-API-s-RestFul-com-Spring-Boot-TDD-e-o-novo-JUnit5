package com.ederfmatos.library.repository;

import com.ederfmatos.library.model.Book;
import com.ederfmatos.library.model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    boolean existsByBookAndReturned(Book book);

}
