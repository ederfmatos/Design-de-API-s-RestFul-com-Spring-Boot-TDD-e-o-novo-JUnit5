package com.ederfmatos.library.bean.loan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanFilterDTO {

    private String customer;

    private String isbn;

}
