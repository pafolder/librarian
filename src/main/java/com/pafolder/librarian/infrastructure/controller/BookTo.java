package com.pafolder.librarian.infrastructure.controller;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookTo {

  @Nullable private String author;
  @Nullable private String title;
  @Nullable private String location;
  @Nullable private Integer amount;
}
