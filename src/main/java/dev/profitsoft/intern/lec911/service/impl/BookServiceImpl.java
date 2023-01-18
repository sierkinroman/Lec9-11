package dev.profitsoft.intern.lec911.service.impl;

import dev.profitsoft.intern.lec911.dto.author.AuthorInfoDto;
import dev.profitsoft.intern.lec911.dto.book.BookDetailsDto;
import dev.profitsoft.intern.lec911.dto.book.BookInfoDto;
import dev.profitsoft.intern.lec911.dto.book.BookSearchDto;
import dev.profitsoft.intern.lec911.dto.book.BookSaveDto;
import dev.profitsoft.intern.lec911.exception.ResourceNotFoundException;
import dev.profitsoft.intern.lec911.model.Author;
import dev.profitsoft.intern.lec911.model.Book;
import dev.profitsoft.intern.lec911.repository.AuthorRepository;
import dev.profitsoft.intern.lec911.repository.BookRepository;
import dev.profitsoft.intern.lec911.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final AuthorRepository authorRepository;

    @Override
    @Transactional
    public long save(BookSaveDto dto) {
        validateDto(dto);
        Book book = new Book();
        updateBookFromDto(book, dto);
        return bookRepository.save(book).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public BookDetailsDto findById(long id) {
        Book book = getOrThrow(id);
        return convertToBookDetails(book);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDetailsDto> findAll() {
        return bookRepository.findAll().stream()
                .map(this::convertToBookDetails)
                .toList();
    }

    @Override
    @Transactional
    public void update(long id, BookSaveDto dto) {
        validateDto(dto);
        Book book = getOrThrow(id);
        updateBookFromDto(book, dto);
        bookRepository.save(book);
    }

    @Override
    @Transactional
    public void deleteById(long id) {
        Book book = getOrThrow(id);
        bookRepository.deleteById(book.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDetailsDto> searchBook(BookSearchDto dto) {
        Author author = resolveAuthor(dto.getAuthorId());
        Integer year = dto.getYear();
        Pageable pageable = dto.getPage() == null || dto.getSize() == null
                ? Pageable.unpaged()
                : PageRequest.of(dto.getPage() - 1, dto.getSize());

        Page<Book> books = resolveSearch(author, year, pageable);

        return books.stream()
                .map(this::convertToBookDetails)
                .toList();
    }

    private Page<Book> resolveSearch(Author author, Integer year, Pageable pageable) {
        LocalDate dateStart = null;
        LocalDate dateEnd = null;
        if (year != null) {
            dateStart = LocalDate.of(year, 1, 1);
            dateEnd = LocalDate.of(year, 12, 31);
        }

        if (author != null && year == null) {
            return bookRepository.findAllByAuthor(author, pageable);
        } else if (year != null && author == null) {
            return bookRepository.findAllByPublishedDateBetween(dateStart, dateEnd, pageable);
        } else if (author != null && year != null) {
            return bookRepository.findAllByAuthorAndPublishedDateBetween(author, dateStart, dateEnd, pageable);
        }

        return bookRepository.findAll(pageable);
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

    private Book getOrThrow(long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book with id %d not found".formatted(bookId)));
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
