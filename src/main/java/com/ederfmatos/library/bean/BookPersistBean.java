package com.ederfmatos.library.bean;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BookPersistBean {

    private long id;
    private String title;
    private String author;
    private String isbn;

}
