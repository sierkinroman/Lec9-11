package dev.profitsoft.intern.lec911.controller;

import dev.profitsoft.intern.lec911.dto.author.AuthorDetailsDto;
import dev.profitsoft.intern.lec911.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @GetMapping
    public ResponseEntity<Object> getAllAuthors() {
        List<AuthorDetailsDto> authors = authorService.findAll();
        if (authors.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(authors);
        }
    }

}
