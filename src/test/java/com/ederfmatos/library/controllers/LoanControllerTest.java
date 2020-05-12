package com.ederfmatos.library.controllers;

import com.ederfmatos.library.bean.loan.LoanDTO;
import com.ederfmatos.library.bean.loan.LoanFilterDTO;
import com.ederfmatos.library.bean.loan.LoanReturnedDTO;
import com.ederfmatos.library.controller.LoanController;
import com.ederfmatos.library.exception.BusinessException;
import com.ederfmatos.library.model.Book;
import com.ederfmatos.library.model.Loan;
import com.ederfmatos.library.service.BookService;
import com.ederfmatos.library.service.LoanService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static com.ederfmatos.library.builder.LoanBuilder.oneLoan;
import static com.ederfmatos.library.builder.LoanDTOBuilder.oneLoanDTO;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = LoanController.class)
@AutoConfigureMockMvc
public class LoanControllerTest {

    final String LOAN_ROUTE = "/loans";

    @Autowired
    MockMvc mock;

    @MockBean
    private LoanService loanService;

    @MockBean
    private BookService bookService;

    @BeforeEach
    public void setup() {

    }

    @Test
    @DisplayName("Deve realizar um empréstimo")
    public void createLoanTest() throws Exception {
        LoanDTO loanDto = oneLoanDTO().build();
        String json = oneLoanDTO().inJson();

        Book book = oneBook()
                .withIsbn(loanDto.getIsbn())
                .withId(1)
                .build();

        Loan loan = oneLoan().withId(1).withBook(book).build();

        given(bookService.getBookByIsbn(any(String.class))).willReturn(Optional.of(book));

        given(loanService.save(any(Loan.class))).willReturn(loan);

        MockHttpServletRequestBuilder request = post(LOAN_ROUTE)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(json);

        mock
                .perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().string("1"));
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar fazer emprestimo de um livro inexistente")
    public void createInvalidLoanTest() throws Exception {
        String json = oneLoanDTO().inJson();

        given(bookService.getBookByIsbn(any(String.class))).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = post(LOAN_ROUTE)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(json);

        mock
                .perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Book not found for this isbn"));
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar fazer emprestimo de um livro inexistente")
    public void shoundReturnErrorOnTryCreateLoanBookTest() throws Exception {
        LoanDTO loanDto = oneLoanDTO().build();
        String json = oneLoanDTO().inJson();

        Book book = oneBook()
                .withIsbn(loanDto.getIsbn())
                .withId(1)
                .build();

        given(bookService.getBookByIsbn(any(String.class))).willReturn(Optional.of(book));

        given(loanService.save(any(Loan.class))).willThrow(new BusinessException("Book already loaned"));

        MockHttpServletRequestBuilder request = post(LOAN_ROUTE)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(json);

        mock
                .perform(request)
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Book already loaned"));
    }

    @Test
    @DisplayName("Deve marcar um livro como retornado")
    public void returnBookTest() throws Exception {
        LoanReturnedDTO loanReturnedDTO = new LoanReturnedDTO(true);

        String json = new ObjectMapper().writeValueAsString(loanReturnedDTO);

        Loan loan = oneLoan().build();

        given(loanService.findById(1)).willReturn(Optional.of(loan));

        MockHttpServletRequestBuilder request = patch(LOAN_ROUTE.concat("/1"))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(json);

        mock
                .perform(request)
                .andExpect(status().isNoContent());

        verify(loanService, times(1)).update(loan);
    }

    @Test
    @DisplayName("Deve lançar exceção quando tentar retornar um emprestimo inexistente")
    public void returnExceptionWhenBookNotFoundBookTest() throws Exception {
        LoanReturnedDTO loanReturnedDTO = new LoanReturnedDTO(true);

        String json = new ObjectMapper().writeValueAsString(loanReturnedDTO);

        doReturn(Optional.empty()).when(loanService).findById(anyLong());

        MockHttpServletRequestBuilder request = patch(LOAN_ROUTE.concat("/1"))
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(json);

        mock
                .perform(request)
                .andExpect(status().isNotFound());

        verify(loanService, never()).update(any(Loan.class));
    }

    @Test
    @DisplayName("Deve filtrar empréstimos")
    public void findLoanTest() throws Exception {
        final long id = 1;

        Loan loan = oneLoan().withId(id).build();

        given(loanService.find(any(LoanFilterDTO.class), any(Pageable.class)))
                .willReturn(new PageImpl<>(singletonList(loan), PageRequest.of(0, 100), 1));

        String queryParams = String.format("?isbn=%s&customer=%s&page=0&size=100", loan.getBook().getIsbn(), loan.getCustomer());

        MockHttpServletRequestBuilder request = get(LOAN_ROUTE.concat(queryParams))
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
