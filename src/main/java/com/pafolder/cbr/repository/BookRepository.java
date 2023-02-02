package com.pafolder.cbr.repository;

import com.pafolder.cbr.model.Book;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface BookRepository extends JpaRepository<Book, Integer> {
    @Cacheable(value = "books")
    @Query("SELECT b FROM Book b WHERE b.author = :author")
    List<Book> findAllByAuthor(String author);

    @Cacheable(value = "books")
    Optional<Book> findById(int id);

    @Transactional
    @Query("UPDATE Book b SET b.amount=:amount WHERE b.id=:id")
    @Modifying
    void updateAmount(@Parameter int id, @Parameter int amount);
}