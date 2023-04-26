package com.pafolder.librarian.infrastructure.controller.profile;

import static com.pafolder.librarian.infrastructure.controller.ControllerUtil.getFilteredBooksJson;

import com.pafolder.librarian.domain.model.Book;
import com.pafolder.librarian.infrastructure.repository.BookRepository;
import com.pafolder.librarian.infrastructure.controller.view.BookView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Tag(name = "1 profile-book-controller")
@RequestMapping(value = BookController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class BookController {
  public static final String REST_URL = "/api/profile/books";
  public static final String NO_BOOK_FOUND = "No book found";
  private final Logger log = LoggerFactory.getLogger(getClass());
  protected BookRepository bookRepository;

  @GetMapping("/search")
  @Operation(
      summary = "Searching for books by Author or substring in title",
      security = {@SecurityRequirement(name = "basicScheme")})
  @Parameter(name = "author", description = "Author name")
  @Parameter(name = "text", description = "Text substring in Book's title (ignoring case)")
  public MappingJacksonValue search(
      @RequestParam @Nullable String author, @RequestParam @Nullable String text) {
    log.info("search()");
    List<Book> books = new ArrayList<>();
    if (Optional.ofNullable(author).isPresent()) {
      books.addAll(bookRepository.findAllByAuthor(author));
    }
    if (Optional.ofNullable(text).isPresent()) {
      books.addAll(bookRepository.findAllBySubstringInTitle(text));
    }
    return getFilteredBooksJson(books.stream().map(BookView::new).collect(Collectors.toList()));
  }
}
