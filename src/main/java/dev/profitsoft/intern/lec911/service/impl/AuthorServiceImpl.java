package dev.profitsoft.intern.lec911.service.impl;

import dev.profitsoft.intern.lec911.dto.author.AuthorDetailsDto;
import dev.profitsoft.intern.lec911.dto.book.BookInfoDto;
import dev.profitsoft.intern.lec911.model.Author;
import dev.profitsoft.intern.lec911.model.Book;
import dev.profitsoft.intern.lec911.repository.AuthorRepository;
import dev.profitsoft.intern.lec911.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    @Override
    public List<AuthorDetailsDto> findAll() {
        return authorRepository.findAll().stream()
                .map(this::convertToAuthorDetails)
                .toList();
    }

    private AuthorDetailsDto convertToAuthorDetails(Author author) {
        return AuthorDetailsDto.builder()
                .firstName(author.getFirstName())
                .lastName(author.getLastName())
                .books(bookToBookInfo(author.getBooks()))
                .build();
    }

    private List<BookInfoDto> bookToBookInfo(Set<Book> books) {
        return books.stream()
                .map(book -> new BookInfoDto(book.getTitle(), book.getPublishedDate()))
                .toList();
    }

}
