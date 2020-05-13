package com.ederfmatos.library.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "book")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String title;

    @Column
    private String author;

    @Column
    private String isbn;

    @OneToMany(mappedBy = "book", fetch = FetchType.LAZY)
    private Set<Loan> loans = new HashSet<>();

}
