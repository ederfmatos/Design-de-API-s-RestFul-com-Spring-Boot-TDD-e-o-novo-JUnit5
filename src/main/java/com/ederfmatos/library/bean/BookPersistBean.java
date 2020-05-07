package com.ederfmatos.library.bean;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BookPersistBean {

    private long id;
    private String title;
    private String author;
    private String isbn;

}
