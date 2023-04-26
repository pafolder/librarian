package com.pafolder.librarian.infrastructure.repository;

import com.pafolder.librarian.domain.model.Book;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer>,
    com.pafolder.librarian.domain.repository.BookRepository {

  Optional<Book> findById(int id);

  @Cacheable(value = "books")
  @Query("SELECT b FROM Book b WHERE b.author = :author ORDER BY b.author, b.title")
  List<Book> findAllByAuthor(String author);

  @Query(
      "SELECT b FROM Book b WHERE b.title ILIKE '%%' + :substring + '%%' ORDER BY b.author,"
          + " b.title")
  List<Book> findAllBySubstringInTitle(String substring);

  @Query("SELECT b FROM Book b WHERE b.id BETWEEN :fromId AND :toId ORDER BY b.id")
  List<Book> findAllFromIdToId(int fromId, int toId);

  @Query("UPDATE Book b SET b.amount = :amount WHERE b.id = :id")
  @Modifying
  void updateAmount(@Parameter int id, @Parameter int amount);

  @CacheEvict(value = "books", allEntries = true)
  Book save(Book book);
}
