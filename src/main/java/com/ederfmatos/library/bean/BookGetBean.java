package com.ederfmatos.library.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BookGetBean {

    private long id;
    private String title;
    private String author;
    private String isbn;

}
