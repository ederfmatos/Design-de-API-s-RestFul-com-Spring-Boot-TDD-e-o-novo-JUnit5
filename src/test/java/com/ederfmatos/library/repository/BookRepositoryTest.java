package com.ederfmatos.library.repository;

import com.ederfmatos.library.builder.BookBuilder;
import com.ederfmatos.library.model.Book;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
public class BookRepositoryTest {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    BookRepository repository;

    @Test
    @DisplayName("Deve retornar verdadeiro quando existir um livro na base com o isbn informado")
    public void shouldBeReturnTrueWhenExistsBookWithIsbnDuplicated() {
        final String ISBN = "123123";

        Book book = oneBook().withIsbn("123123").build();

        entityManager.persist(book);

        boolean exists = repository.existsByIsbn(ISBN);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve retornar falso quando não existir um livro na base com o isbn informado")
    public void shouldBeReturnFalseWhenIsbnDoenst() {
        final String ISBN = "123123";

        boolean exists = repository.existsByIsbn(ISBN);

        assertThat(exists).isFalse();
    }

}
