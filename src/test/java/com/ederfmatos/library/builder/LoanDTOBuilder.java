package com.ederfmatos.library.builder;

import com.ederfmatos.library.bean.loan.LoanDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LoanDTOBuilder {

    private LoanDTO loan;

    private LoanDTOBuilder() {
        setDefaultAttributes();
    }

    private void setDefaultAttributes() {
        loan = LoanDTO.builder().isbn("123123").customer("Customer").build();
    }

    public static LoanDTOBuilder oneLoanDTO() {
        return new LoanDTOBuilder();
    }

    public LoanDTOBuilder withIsbn(String isbn) {
        loan.setIsbn(isbn);
        return this;
    }

    public LoanDTOBuilder withCustomer(String customer) {
        loan.setCustomer(customer);
        return this;
    }

    public LoanDTO build() {
        return loan;
    }

    public String inJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(loan);
    }
}
