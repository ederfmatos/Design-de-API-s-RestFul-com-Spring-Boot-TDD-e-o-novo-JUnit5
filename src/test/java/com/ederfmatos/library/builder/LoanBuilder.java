package com.ederfmatos.library.builder;

import com.ederfmatos.library.model.Book;
import com.ederfmatos.library.model.Loan;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;

import static com.ederfmatos.library.builder.BookBuilder.oneBook;

public class LoanBuilder {

    private Loan loan;

    private LoanBuilder() {
        setDefaultAttributes();
    }

    private void setDefaultAttributes() {
        loan = Loan.builder()
                .book(oneBook().build())
                .customer("Customer")
                .timestamp(LocalDate.now())
                .build();
    }

    public static LoanBuilder oneLoan() {
        return new LoanBuilder();
    }

    public LoanBuilder withId(long id) {
        loan.setId(id);
        return this;
    }

    public LoanBuilder withCustomer(String customer) {
        loan.setCustomer(customer);
        return this;
    }

    public LoanBuilder withBook(Book book) {
        loan.setBook(book);
        return this;
    }

    public LoanBuilder returned() {
        loan.setReturned(true);
        return this;
    }

    public Loan build() {
        return loan;
    }

    public String inJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(loan);
    }
}
