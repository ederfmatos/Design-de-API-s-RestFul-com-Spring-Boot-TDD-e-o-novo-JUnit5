package com.ederfmatos.library.controllers;

import com.ederfmatos.library.bean.BookPersistBean;
import com.ederfmatos.library.exception.BusinessException;
import com.ederfmatos.library.model.Book;
import com.ederfmatos.library.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Optional;

import static com.ederfmatos.library.builder.BookBuilder.oneBook;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

    private static final String BOOK_ROUTE = "/books";

    @Autowired
    private MockMvc mock;

    @MockBean
    private BookService service;

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


}
