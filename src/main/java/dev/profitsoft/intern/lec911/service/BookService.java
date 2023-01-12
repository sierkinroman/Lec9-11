package dev.profitsoft.intern.lec911.service;

import dev.profitsoft.intern.lec911.dto.book.BookDetailsDto;
import dev.profitsoft.intern.lec911.dto.book.BookSearchDto;
import dev.profitsoft.intern.lec911.dto.book.BookSaveDto;

import java.util.List;

public interface BookService {

    long save(BookSaveDto dto);

    BookDetailsDto findById(long id);

    List<BookDetailsDto> findAll();

    void update(long id, BookSaveDto dto);

    void deleteById(long id);

    List<BookDetailsDto> searchBook(BookSearchDto dto);

}
