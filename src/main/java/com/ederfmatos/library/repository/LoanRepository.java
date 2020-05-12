package com.ederfmatos.library.repository;

import com.ederfmatos.library.model.Book;
import com.ederfmatos.library.model.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    boolean existsByBookAndReturnedFalse(Book book);

    Page<Loan> findByBookIsbnOrCustomer(String isbn, String customer, Pageable pageable);

}
