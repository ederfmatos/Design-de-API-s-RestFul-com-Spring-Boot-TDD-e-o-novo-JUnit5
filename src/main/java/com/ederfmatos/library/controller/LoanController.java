package com.ederfmatos.library.controller;

import com.ederfmatos.library.bean.book.BookGetBean;
import com.ederfmatos.library.bean.loan.LoanDTO;
import com.ederfmatos.library.bean.loan.LoanFilterDTO;
import com.ederfmatos.library.bean.loan.LoanReturnedDTO;
import com.ederfmatos.library.model.Loan;
import com.ederfmatos.library.service.BookService;
import com.ederfmatos.library.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static com.ederfmatos.library.lib.LibraryMapper.getMapper;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    @Autowired
    private BookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public long create(@RequestBody LoanDTO loanDTO) {
        return bookService.getBookByIsbn(loanDTO.getIsbn())
                .map(book -> {
                    Loan loan = Loan.builder()
                            .book(book)
                            .customer(loanDTO.getCustomer())
                            .timestamp(LocalDate.now())
                            .build();

                    loan = loanService.save(loan);
                    return loan.getId();
                }).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for this isbn"));
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void returnBook(@PathVariable Long id, @RequestBody LoanReturnedDTO dto) {
        loanService.findById(id).map(loan -> {
            loan.setReturned(dto.isReturned());
            loanService.update(loan);
            return null;
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan not found"));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<LoanDTO> find(LoanFilterDTO dto, Pageable pageRequest) {
        Page<Loan> result = loanService.find(dto, pageRequest);

        List<LoanDTO> loans = result.getContent().stream().map(entity -> getMapper().map(entity, LoanDTO.class)).collect(toList());

        return new PageImpl<>(loans, pageRequest, result.getTotalElements());
    }


}
