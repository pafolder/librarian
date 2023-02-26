package com.pafolder.librarian.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.util.ProxyUtils;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonFilter("bookJsonFilter")
@Table(name = "book", uniqueConstraints = {@UniqueConstraint(columnNames = {"title", "author"},
    name = "book_unique_title_author_idx")})
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || !getClass().equals(ProxyUtils.getUserClass(o))) {
      return false;
    }
    return id != null && id == ((Book) o).id;
  }

  @Override
  public int hashCode() {
    return id == null ? 0 : id;
  }
}
