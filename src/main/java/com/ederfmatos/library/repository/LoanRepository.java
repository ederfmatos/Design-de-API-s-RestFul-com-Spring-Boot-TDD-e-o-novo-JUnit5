package com.ederfmatos.library.repository;

import com.ederfmatos.library.model.Book;
import com.ederfmatos.library.model.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {

    boolean existsByBookAndReturnedFalse(Book book);

    Page<Loan> findByBookIsbnOrCustomer(String isbn, String customer, Pageable pageable);

    Page<Loan> findByBook(Book book, Pageable pageable);

    @Query("select l from Loan as l where l.date <= :threeDaysAgo and l.returned = false")
    List<Loan> findByDateLessThanTodayAndReturnedFalse(@Param("threeDaysAgo") LocalDate threeDaysAgo);
}
