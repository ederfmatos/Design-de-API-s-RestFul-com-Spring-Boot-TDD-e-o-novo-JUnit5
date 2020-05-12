package com.ederfmatos.library.service;

import com.ederfmatos.library.exception.BusinessException;
import com.ederfmatos.library.model.Book;
import com.ederfmatos.library.model.Loan;
import com.ederfmatos.library.repository.LoanRepository;
import com.ederfmatos.library.service.impl.LoanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static com.ederfmatos.library.builder.LoanBuilder.oneLoan;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

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
        when(repository.existsByBookAndReturnedFalse(any(Book.class))).thenReturn(false);

        Loan savedLoan = service.save(loan);

        assertThat(savedLoan.getId()).isNotNull();
        assertThat(savedLoan.getBook()).isEqualTo(loan.getBook());
        assertThat(savedLoan.getCustomer()).isEqualTo(loan.getCustomer());
    }

    @Test
    @DisplayName("Deve lançar erro de negocio ao salvar um livro já emprestado")
    public void loanedBookTest() {
        Loan loan = oneLoan()
                .withId(1)
                .build();

        when(repository.existsByBookAndReturnedFalse(any(Book.class))).thenReturn(true);

        Throwable exception = catchThrowable(() -> service.save(loan));

        assertThat(exception).isInstanceOf(BusinessException.class);
        assertThat(exception.getMessage()).isEqualTo("Book already loaned");

        verify(repository, never()).save(loan);
    }

    @Test
    @DisplayName("Deve buscar um emprestimo pelo id")
    public void getLoanDetailsTest() {
        final long id = 10;

        Loan loan = oneLoan().withId(id).build();

        doReturn(Optional.of(loan)).when(repository).findById(id);

        Optional<Loan> foundLoan = service.findById(id);

        assertThat(foundLoan.isPresent()).isTrue();
        assertThat(foundLoan.get().getId()).isEqualTo(id);
        assertThat(foundLoan.get().getTimestamp()).isEqualTo(loan.getTimestamp());
        assertThat(foundLoan.get().getBook()).isEqualTo(loan.getBook());
        assertThat(foundLoan.get().getCustomer()).isEqualTo(loan.getCustomer());
        verify(repository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Deve atualizar um empréstimo")
    public void updateLoanTest() {
        Loan loan = oneLoan().withId(1).returned().build();

        doReturn(loan).when(repository).save(loan);
        
        Loan updatedLoan = service.update(loan);
        assertThat(updatedLoan.isReturned()).isTrue();

        verify(repository, times(1)).save(loan);
    }

}
