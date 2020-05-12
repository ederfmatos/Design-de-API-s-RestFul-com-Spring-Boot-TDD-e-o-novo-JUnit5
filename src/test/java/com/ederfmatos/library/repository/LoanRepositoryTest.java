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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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

}
