package com.ederfmatos.library.repository;

import com.ederfmatos.library.model.Book;
import com.ederfmatos.library.model.Loan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static com.ederfmatos.library.builder.BookBuilder.oneBook;
import static com.ederfmatos.library.builder.LoanBuilder.oneLoan;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
public class LoanRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    LoanRepository repository;

    @Test
    @DisplayName("Deve verificar se existe um emprestimo não devolvido para um livro")
    public void existsBookAndNotReturned() {
        Book book = oneBook().withIsbn("123123").build();

        entityManager.persist(book);

        Loan loan = oneLoan().withBook(book).build();
        entityManager.persist(loan);

        boolean exists = repository.existsByBookAndReturnedFalse(book);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve buscar empréstimo pelo isbn ou customer")
    public void findByBookIsbnOrCustomerTest() {
        Book book = oneBook().withIsbn("123123").build();

        entityManager.persist(book);

        Loan loan = oneLoan().withBook(book).build();
        entityManager.persist(loan);

        Page<Loan> result = repository.findByBookIsbnOrCustomer(book.getIsbn(), loan.getCustomer(), PageRequest.of(0, 10));

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent()).contains(loan);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve obter emprestimos onde a data de emprestimo for menor que tres dias atras e não retornados")
    public void findByLoanDateLessAndNotReturned() {
        Loan loan = oneLoan().withDate(LocalDate.now().minusDays(5)).build();

        entityManager.persist(loan.getBook());
        entityManager.persist(loan);

        List<Loan> result = repository.findByDateLessThanTodayAndReturnedFalse(LocalDate.now().minusDays(4));

        assertThat(result).hasSize(1).contains(loan);
    }

    @Test
    @DisplayName("Não deve obter emprestimos onde a data de emprestimo for igual à hoje e não retornados")
    public void notFindByLoanDateEqualAndNotReturned() {
        Loan loan = oneLoan().withDate(LocalDate.now()).build();

        entityManager.persist(loan.getBook());
        entityManager.persist(loan);

        List<Loan> result = repository.findByDateLessThanTodayAndReturnedFalse(LocalDate.now().minusDays(4));

        assertThat(result).isEmpty();
    }

}
