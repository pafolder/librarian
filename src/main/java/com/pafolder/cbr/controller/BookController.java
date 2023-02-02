package com.pafolder.cbr.controller;

import com.pafolder.cbr.model.Book;
import com.pafolder.cbr.repository.BookRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@AllArgsConstructor
@Tag(name = "profile-book-controller")
@RequestMapping(value = BookController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class BookController {
    public static final String REST_URL = "/api/profile/books";
    private static final String NO_BOOKS_FOUND = "No books found";
    public static final String NO_BOOK_FOUND = "No book found";
    private final Logger log = LoggerFactory.getLogger(getClass());
    protected BookRepository bookRepository;

    @GetMapping
    @Operation(summary = "Get books by Author", security = {@SecurityRequirement(name = "basicScheme")})
    public List<Book> getAllByAuthor(@RequestParam String author) {
        log.info("getAllByAuthor()");
        List<Book> books = bookRepository.findAllByAuthor(author);
        if (books.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, NO_BOOKS_FOUND);
        }
        return books;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get book by Id", security = {@SecurityRequirement(name = "basicScheme")})
    public Book getById(@PathVariable int id) {
        log.info("getById()");
        return bookRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, NO_BOOK_FOUND));
    }
}
