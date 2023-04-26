package com.pafolder.librarian.infrastructure.repository;

import com.pafolder.librarian.domain.model.Book;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class BookRepositoryTest {

  @Autowired private BookRepository bookRepository;

  @Test
  public void create() {
    var b1 = new Book(null, "test", "test", "test", 1);

    var b2 = bookRepository.save(b1);

    Assertions.assertEquals(b1, b2);
  }
}
