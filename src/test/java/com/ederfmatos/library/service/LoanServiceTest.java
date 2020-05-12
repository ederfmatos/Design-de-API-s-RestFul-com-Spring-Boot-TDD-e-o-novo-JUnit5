package com.ederfmatos.library.service;

import com.ederfmatos.library.model.Loan;
import com.ederfmatos.library.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static com.ederfmatos.library.builder.LoanBuilder.oneLoan;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    @MockBean
    private LoanService service;

    @MockBean
    LoanRepository repository;

    @BeforeEach
    public void setup() {
        this.service = new LoanServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um empréstimo")
    public void saveBookTest() {
        Loan loan = oneLoan()
                .withId(1)
                .build();

        when(repository.save(loan)).thenReturn(oneLoan().withId(1).build());

        Loan savedLoan = service.save(loan);

        assertThat(savedLoan.getId()).isNotNull();
        assertThat(savedLoan.getBook()).isEqualTo(loan.getBook());
        assertThat(savedLoan.getCustomer()).isEqualTo(loan.getCustomer());
    }

}
