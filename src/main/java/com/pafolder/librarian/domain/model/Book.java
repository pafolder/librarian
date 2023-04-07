package com.pafolder.librarian.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "book",
    uniqueConstraints = {
      @UniqueConstraint(
          columnNames = {"title", "author"},
          name = "book_unique_title_author_idx")
    })
public class Book {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Integer id;

  @Column(name = "author", nullable = false)
  @NotBlank
  private String author;

  @Column(name = "title", nullable = false)
  @NotBlank
  private String title;

  @Column(name = "location", nullable = false)
  @NotBlank
  private String location;

  @Column(name = "amount")
  @Min(0)
  @Max(1)
  private int amount;
}
