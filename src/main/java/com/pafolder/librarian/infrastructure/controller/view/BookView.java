package com.pafolder.librarian.infrastructure.controller.view;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.pafolder.librarian.domain.model.Book;
import lombok.RequiredArgsConstructor;

@JsonInclude(Include.NON_NULL)
@JsonFilter("bookJsonFilter")
@RequiredArgsConstructor
public class BookView {

  private final Book book;

  public Integer getId() {
    return book.getId();
  }

  public String getAuthor() {
    return book.getAuthor();
  }

  public String getTitle() {
    return book.getTitle();
  }

  public String getLocation() {
    return book.getLocation();
  }

  public int getAmount() {
    return book.getAmount();
  }
}
