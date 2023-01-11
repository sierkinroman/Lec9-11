package dev.profitsoft.intern.lec911.service.impl;

import dev.profitsoft.intern.lec911.dto.author.AuthorInfoDto;
import dev.profitsoft.intern.lec911.dto.book.BookDetailsDto;
import dev.profitsoft.intern.lec911.dto.book.BookInfoDto;
import dev.profitsoft.intern.lec911.dto.book.BookQueryDto;
import dev.profitsoft.intern.lec911.dto.book.BookSaveDto;
import dev.profitsoft.intern.lec911.exception.ResourceNotFoundException;
import dev.profitsoft.intern.lec911.model.Author;
import dev.profitsoft.intern.lec911.model.Book;
import dev.profitsoft.intern.lec911.repository.AuthorRepository;
import dev.profitsoft.intern.lec911.repository.BookRepository;
import dev.profitsoft.intern.lec911.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final AuthorRepository authorRepository;

    @Override
    public long save(BookSaveDto dto) {
        validateDto(dto);
        Book book = new Book();
        updateBookFromDto(book, dto);
        return bookRepository.save(book).getId();
    }

    @Override
    public BookDetailsDto findById(long id) {
        Book book = getOrThrow(id);
        return convertToBookDetails(book);
    }

    @Override
    public List<BookDetailsDto> findAll() {
        return bookRepository.findAll().stream()
                .map(this::convertToBookDetails)
                .toList();
    }

    @Override
    public void update(long id, BookSaveDto dto) {
        validateDto(dto);
        Book book = getOrThrow(id);
        updateBookFromDto(book, dto);
        bookRepository.save(book);
    }

    @Override
    public void deleteById(long id) {
        Book book = getOrThrow(id);
        bookRepository.deleteById(book.getId());
    }

    // TODO search
    @Override
    public List<BookDetailsDto> search(BookQueryDto dto) {
        Author author = authorRepository.findById(dto.getAuthorId()).get();
        int year = dto.getYear();
        List<Book> books = bookRepository.searchAllByAuthorOrYear(null, year);
        return books.stream()
                .map(this::convertToBookDetails)
                .toList();
    }

    private void validateDto(BookSaveDto dto) {
        bookRepository.findByIsbn(dto.getIsbn()).ifPresent(book -> {
            throw new IllegalArgumentException("book with given isbn already exists");
        });

        if (dto.getPublishedDate() != null && dto.getPublishedDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("publishedDate should be before now");
        }
    }

    private void updateBookFromDto(Book book, BookSaveDto dto) {
        book.setTitle(dto.getTitle());
        book.setIsbn(dto.getIsbn());
        book.setPublishedDate(dto.getPublishedDate());
        book.setAuthor(resolveAuthor(dto.getAuthorId()));
    }

    private Author resolveAuthor(Long authorId) {
        if (authorId == null) {
            return null;
        }
        return authorRepository.findById(authorId).
                orElseThrow(() -> new IllegalArgumentException("Author with id %d not found".formatted(authorId)));
    }

    private Book getOrThrow(long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book with id %d not found".formatted(id)));
    }

    private BookDetailsDto convertToBookDetails(Book book) {
        return BookDetailsDto.builder()
                .bookInfo(new BookInfoDto(book.getId(), book.getTitle(), book.getPublishedDate()))
                .isbn(book.getIsbn())
                .author(convertToAuthorInfo(book.getAuthor()))
                .build();
    }

    private AuthorInfoDto convertToAuthorInfo(Author author) {
        if (author == null) {
            return null;
        }

        return new AuthorInfoDto(author.getId(), author.getFirstName(), author.getLastName());
    }

}
