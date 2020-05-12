package com.ederfmatos.library.repository;

import com.ederfmatos.library.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbn(String ISBN);

    Optional<Book> findByIsbn(String isbn);

}
