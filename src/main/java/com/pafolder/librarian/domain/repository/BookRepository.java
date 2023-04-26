package com.pafolder.librarian.domain.repository;

import com.pafolder.librarian.domain.model.Book;
import java.util.List;
import java.util.Optional;

public interface BookRepository {

  Book save(Book book);
  Optional<Book> findById(int id);

  List<Book> findAllByAuthor(String author);

  List<Book> findAllBySubstringInTitle(String substring);

  List<Book> findAllFromIdToId(int fromId, int toId);

  void updateAmount(int id, int amount);
}
