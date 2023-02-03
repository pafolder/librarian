package com.pafolder.librarian.controller.admin;

import com.pafolder.librarian.model.Book;
import com.pafolder.librarian.repository.BookRepository;
import com.pafolder.librarian.to.BookTo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

import static com.pafolder.librarian.controller.admin.AdminBookController.REST_URL;
import static com.pafolder.librarian.util.JsonFilter.getFilteredBooksJson;

@RestController
@AllArgsConstructor
@Tag(name = "5 admin-book-controller")
@RequestMapping(value = REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class AdminBookController {
    public static final String REST_URL = "/api/admin/books";
    public static final String NO_BOOK_FOR_UPDATE_FOUND = "No book for update found";
    private static final String NO_BOOK_FOR_DELETION_FOUND = "No book for deletion found";
    private final Logger log = LoggerFactory.getLogger(getClass());
    private BookRepository bookRepository;

    @GetMapping()
    @Operation(summary = "Get books with Ids between fromId and toId",
            security = {@SecurityRequirement(name = "basicScheme")})
    @Parameter(name = "fromId", description = "From Id")
    @Parameter(name = "toId", description = "To Id")
    public MappingJacksonValue getAllFromIdToId(
            @RequestParam(defaultValue = "1") int fromId, @RequestParam @Nullable Integer toId) {
        log.info("getAllFromIdToId()");
        return getFilteredBooksJson(bookRepository
                .findAllFromIdToId(fromId, Optional.ofNullable(toId).orElse(0)));
    }

    @PostMapping
    @CacheEvict(cacheNames = {"books"}, allEntries = true)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new Book", security = {@SecurityRequirement(name = "basicScheme")})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Provide Book data")
    @Transactional
    public ResponseEntity<Book> create(@Valid @RequestBody BookTo bookTo) {
        log.info("create()");
        Book created = bookRepository.save(new Book(null, bookTo.getAuthor(), bookTo.getTitle(),
                bookTo.getLocation(), bookTo.getAmount()));
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping("/{id}")
    @CacheEvict(cacheNames = {"books"}, allEntries = true)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @Operation(summary = "Update Book", security = {@SecurityRequirement(name = "basicScheme")})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Provide updated Book data")
    @Transactional
    public void update(@PathVariable int id, @Valid @RequestBody BookTo bookTo) {
        log.info("update()");
        Book existing = bookRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, NO_BOOK_FOR_UPDATE_FOUND));
        Optional.ofNullable(bookTo.getAuthor()).ifPresent(existing::setAuthor);
        Optional.ofNullable(bookTo.getTitle()).ifPresent(existing::setTitle);
        Optional.ofNullable(bookTo.getLocation()).ifPresent(existing::setLocation);
        Optional.ofNullable(bookTo.getAmount()).ifPresent(existing::setAmount);
        bookRepository.save(existing);
    }

    @DeleteMapping("/{id}")
    @CacheEvict(cacheNames = {"books"}, allEntries = true)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete Book", security = {@SecurityRequirement(name = "basicScheme")})
    @Parameter(name = "id", description = "Book Id to delete")
    @Transactional
    public void delete(@PathVariable int id) {
        log.info("delete()");
        bookRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, NO_BOOK_FOR_DELETION_FOUND));
        bookRepository.deleteById(id);
    }
}
