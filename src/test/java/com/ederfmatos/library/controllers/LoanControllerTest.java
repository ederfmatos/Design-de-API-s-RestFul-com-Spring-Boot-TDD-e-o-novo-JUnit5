package com.ederfmatos.library.controllers;

import com.ederfmatos.library.bean.loan.LoanDTO;
import com.ederfmatos.library.controller.LoanController;
import com.ederfmatos.library.exception.BusinessException;
import com.ederfmatos.library.model.Book;
import com.ederfmatos.library.model.Loan;
import com.ederfmatos.library.service.BookService;
import com.ederfmatos.library.service.LoanService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static com.ederfmatos.library.builder.LoanBuilder.oneLoan;
import static com.ederfmatos.library.builder.LoanDTOBuilder.oneLoanDTO;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    private BookService bookService;

    @MockBean
    private LoanService loanService;

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

}
