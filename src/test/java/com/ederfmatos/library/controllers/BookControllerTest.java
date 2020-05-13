package com.ederfmatos.library.controllers;

import com.ederfmatos.library.bean.book.BookPersistBean;
import com.ederfmatos.library.builder.BookBuilder;
import com.ederfmatos.library.controller.BookController;
import com.ederfmatos.library.exception.BusinessException;
import com.ederfmatos.library.model.Book;
import com.ederfmatos.library.service.BookService;
import com.ederfmatos.library.service.LoanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Optional;

import static com.ederfmatos.library.builder.BookBuilder.oneBook;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = BookController.class)
@AutoConfigureMockMvc
public class BookControllerTest {

    private static final String BOOK_ROUTE = "/books";

    @Autowired
    private MockMvc mock;

    @MockBean
    private BookService service;

    @MockBean
    private LoanService loanService;

    @Test
    @DisplayName("Deve criar um livro")
    public void createBookTest() throws Exception {
        BookPersistBean bean = new BookPersistBean(0, "Title", "Author", "123123");
        Book book = oneBook().withId(1).build();

        given(service.save(Mockito.any(Book.class))).willReturn(book);

        String json = new ObjectMapper().writeValueAsString(bean);

        MockHttpServletRequestBuilder request = post(BOOK_ROUTE)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(json);

        mock
                .perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("title").value(book.getTitle()))
                .andExpect(jsonPath("author").value(book.getAuthor()))
                .andExpect(jsonPath("isbn").value(book.getIsbn()))
        ;
    }

    @Test
    @DisplayName("Deve lançar erro de validação quando não houver dados suficientes para criação do livro")
    public void createInvalidBookTest() throws Exception {
        String json = new ObjectMapper().writeValueAsString(new BookPersistBean());

        MockHttpServletRequestBuilder request = post(BOOK_ROUTE)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(json);

        mock
                .perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(3)));
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar livro com isbn já cadastrado por outro livro")
    public void createBookWithDuplicatedIsbn() throws Exception {
        Book book = oneBook().build();

        String json = new ObjectMapper().writeValueAsString(book);

        BusinessException exception = new BusinessException("Isbn já cadastrado");
        given(service.save(Mockito.any(Book.class))).willThrow(exception);

        MockHttpServletRequestBuilder request = post(BOOK_ROUTE)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(json);

        mock
                .perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value(exception.getMessage()));
    }

    @Test
    @DisplayName("Deve obter informações de um livro")
    public void getBookDetail() throws Exception {
        final long id = 1;

        Book book = oneBook().build();
        given(service.getById(id)).willReturn(Optional.of(book));

        MockHttpServletRequestBuilder request = get(BOOK_ROUTE.concat("/").concat(String.valueOf(id)))
                .accept(APPLICATION_JSON);
        mock
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("title").value(book.getTitle()))
                .andExpect(jsonPath("author").value(book.getAuthor()))
                .andExpect(jsonPath("isbn").value(book.getIsbn()));
    }

    @Test
    @DisplayName("Deve retornar resource not found quando um livro não existir")
    public void bookNotFoundTest() throws Exception {
        final long id = 1;

        given(service.getById(anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = get(BOOK_ROUTE.concat("/").concat(String.valueOf(id)))
                .accept(APPLICATION_JSON);
        mock
                .perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest() throws Exception {
        final long id = 1;

        Book book = oneBook().build();
        given(service.getById(anyLong())).willReturn(Optional.of(book));

        MockHttpServletRequestBuilder request = delete(BOOK_ROUTE.concat("/").concat(String.valueOf(id)));

        mock
                .perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar resourceNotFound quando nao encontrar um livro para deletar")
    public void deleteNotFoundBookTest() throws Exception {
        given(service.getById(anyLong())).willReturn(Optional.empty());
        MockHttpServletRequestBuilder request = delete(BOOK_ROUTE.concat("/").concat(String.valueOf(1)));

        mock
                .perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve atualizar um livro")
    public void updateBooktest() throws Exception {
        final long id = 1;

        Book book = oneBook().withAuthor("Teste").withId(id).build();

        given(service.getById(anyLong())).willReturn(Optional.of(book));

        BookBuilder bookBuilder = oneBook().withId(id).withAuthor("Teste 2");

        MockHttpServletRequestBuilder request = put(BOOK_ROUTE.concat("/").concat(String.valueOf(id)))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(bookBuilder.inJson());

        mock
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("title").value(book.getTitle()))
                .andExpect(jsonPath("author").value("Teste 2"))
                .andExpect(jsonPath("isbn").value(book.getIsbn()));
    }

    @Test
    @DisplayName("Deve retornar exceção ao tentar atualizar um livro inexistente")
    public void updateNotFoundBooktest() throws Exception {
        final long id = 1;

        given(service.getById(anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = put(BOOK_ROUTE.concat("/").concat(String.valueOf(id)))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(oneBook().withId(id).withAuthor("Teste").inJson());

        mock
                .perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve filtrar livros")
    public void findBooktT() throws Exception {
        final long id = 1;

        Book book = oneBook().withId(id).build();

        given(service.find(any(Book.class), any(Pageable.class)))
                .willReturn(new PageImpl<>(singletonList(book), PageRequest.of(0, 100), 1));

        String queryParams = String.format("?title=%s&author=%s&page=0&size=100", book.getTitle(), book.getAuthor());

        MockHttpServletRequestBuilder request = get(BOOK_ROUTE.concat(queryParams))
                .accept(APPLICATION_JSON);
        mock
                .perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value(0));
    }

}
