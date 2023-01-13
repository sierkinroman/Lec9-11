package dev.profitsoft.intern.lec911.controller;

import dev.profitsoft.intern.lec911.dto.RestResponse;
import dev.profitsoft.intern.lec911.dto.book.BookDetailsDto;
import dev.profitsoft.intern.lec911.dto.book.BookSearchDto;
import dev.profitsoft.intern.lec911.dto.book.BookSaveDto;
import dev.profitsoft.intern.lec911.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RestResponse createBook(@Valid @RequestBody BookSaveDto dto) {
        long id = bookService.save(dto);
        return new RestResponse(String.valueOf(id));
    }

    @GetMapping("/{id}")
    public BookDetailsDto getBook(@PathVariable long id) {
        return bookService.findById(id);
    }

    @GetMapping
    public ResponseEntity<List<BookDetailsDto>> getAllBooks() {
        List<BookDetailsDto> books = bookService.findAll();
        return books.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(books);
    }

    @PutMapping("/{id}")
    public RestResponse updateBook(@PathVariable long id, @Valid @RequestBody BookSaveDto dto) {
        bookService.update(id, dto);
        return new RestResponse("Update was successful");
    }

    @PostMapping("/_search")
    public ResponseEntity<List<BookDetailsDto>> searchBook(@Valid @RequestBody BookSearchDto dto) {
        List<BookDetailsDto> books = bookService.searchBook(dto);
        return books.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(books);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public RestResponse deleteBook(@PathVariable long id) {
        bookService.deleteById(id);
        return new RestResponse("Delete was successful");
    }

}
