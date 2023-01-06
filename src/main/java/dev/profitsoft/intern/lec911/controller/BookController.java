package dev.profitsoft.intern.lec911.controller;

import dev.profitsoft.intern.lec911.dto.RestResponse;
import dev.profitsoft.intern.lec911.dto.book.BookDetailsDto;
import dev.profitsoft.intern.lec911.dto.book.BookQueryDto;
import dev.profitsoft.intern.lec911.dto.book.BookSaveDto;
import dev.profitsoft.intern.lec911.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Object> getAllBooks() {
        List<BookDetailsDto> books = bookService.findAll();
        if (books.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(books);
        }
    }

    @PutMapping("/{id}")
    public RestResponse updateBook(@Valid @PathVariable long id, @RequestBody BookSaveDto dto) {
        bookService.update(id, dto);
        return new RestResponse("OK");
    }

    @PostMapping("/_search")
    public List<BookDetailsDto> search(@RequestBody BookQueryDto query) {
        return bookService.search(query);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public RestResponse deleteBook(@PathVariable long id) {
        bookService.deleteById(id);
        return new RestResponse("OK");
    }

}
