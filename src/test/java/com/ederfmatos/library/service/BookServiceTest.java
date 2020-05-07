package com.ederfmatos.library.service;

import com.ederfmatos.library.exception.BusinessException;
import com.ederfmatos.library.model.Book;
import com.ederfmatos.library.repository.BookRepository;
import com.ederfmatos.library.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.ederfmatos.library.builder.BookBuilder.oneBook;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    private BookService service;

    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setup() {
        this.service = new BookServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest() {
        Book book = oneBook().build();

        when(repository.save(book)).thenReturn(oneBook().withId(1).build());

        Book savedBook = service.save(book);

        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo(book.getTitle());
        assertThat(savedBook.getAuthor()).isEqualTo(book.getAuthor());
        assertThat(savedBook.getIsbn()).isEqualTo(book.getIsbn());
    }

    @Test
    @DisplayName("Deve lançar uma exceção ao tentar salvar livro com isbn já cadastrado por outro livro")
    public void shouldNotSaveABookWithDuplicatedISBN() {
        Book book = oneBook().build();

        doReturn(true).when(repository).existsByIsbn();
        Throwable exception = catchThrowable(() -> service.save(book));

        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn já cadastrado");

        verify(repository, never()).save(book);
    }

}
