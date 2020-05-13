package com.ederfmatos.library.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String customer;

    @JoinColumn(name = "book_id")
    @ManyToOne
    private Book book;

    @Column
    private LocalDate date;

    @Column
    private boolean returned;

    @Column(name = "customer_email")
    private String customerEmail;

}
