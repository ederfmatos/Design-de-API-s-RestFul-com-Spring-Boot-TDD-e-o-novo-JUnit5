package com.ederfmatos.library.service;

import com.ederfmatos.library.exception.BusinessException;
import com.ederfmatos.library.model.Book;
import com.ederfmatos.library.repository.BookRepository;
import com.ederfmatos.library.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static com.ederfmatos.library.builder.BookBuilder.oneBook;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    @MockBean
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

        doReturn(true).when(repository).existsByIsbn(book.getIsbn());
        Throwable exception = catchThrowable(() -> service.save(book));

        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn já cadastrado");

        verify(repository, never()).save(book);
    }

    @Test
    @DisplayName("Deve obter um livro por id")
    public void getByIdTest() {
        final long id = 1;

        Book book = oneBook().withId(id).build();
        doReturn(Optional.of(book)).when(repository).findById(id);

        Optional<Book> foundBook = service.getById(id);

        assertThat(foundBook.isPresent()).isTrue();
        assertThat(foundBook.get().getId()).isEqualTo(book.getId());
        assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
        assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
    }

    @Test
    @DisplayName("Deve obter vazio quando buscar um livro inexistente")
    public void getEmptyBookByIdTest() {
        final long id = 1;

        doReturn(Optional.empty()).when(repository).findById(id);
        Optional<Book> book = service.getById(id);

        assertThat(book.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Deve deletar um book")
    public void deleteBookTest() {
        Book book = oneBook().withId(1).build();

        assertDoesNotThrow(() -> doNothing().when(repository).delete(book));

        service.deleteById(book);

        verify(repository, times(1)).delete(book);
    }

    @Test
    @DisplayName("Deve ocorrer um erro ao tentar deletar livro inexistente")
    public void deleteInvalidBookTest() {
        Book book = null;

        assertThrows(IllegalArgumentException.class, () -> service.deleteById(book));

        verify(repository, never()).delete(book);
    }

    @Test
    @DisplayName("Deve alterar um book pelo id")
    public void updateBookTest() {
        Book book = oneBook().withId(1).build();

        doReturn(book).when(repository).save(book);
        assertDoesNotThrow(() -> service.update(book));
    }

    @Test
    @DisplayName("Deve ocorrer erro ao tentar atualizar livro inexistente")
    public void updateInvalidBookTest() {
        assertThrows(IllegalArgumentException.class, () -> service.update(null));

        verify(repository, never()).save(null);
    }

    @Test
    @DisplayName("Deve filtrar livros")
    public void findBookTest() {
        Book book = oneBook().build();

        List<Book> books = List.of(book);

        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Book> page = new PageImpl<Book>(books, pageRequest, 1);
        doReturn(page).when(repository).findAll(any(Example.class), any(PageRequest.class));

        Page<Book> result = service.find(book, pageRequest);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(books);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("Deve obter um livro pelo isbn")
    public void getBookByIsbnTest() {
        final String isbn = "123123";

        Book book = oneBook().withId(1).withIsbn(isbn).build();

        when(service.getBookByIsbn(anyString())).thenReturn(Optional.of(book));

        Optional<Book> foundBook = service.getBookByIsbn(isbn);

        assertThat(foundBook.isPresent()).isTrue();
        assertThat(foundBook.get().getId()).isEqualTo(book.getId());
        assertThat(foundBook.get().getIsbn()).isEqualTo(isbn);

        verify(repository, times(1)).findByIsbn(isbn);
    }

}
