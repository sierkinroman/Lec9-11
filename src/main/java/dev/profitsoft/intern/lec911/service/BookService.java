package dev.profitsoft.intern.lec911.service;

import dev.profitsoft.intern.lec911.dto.book.BookDetailsDto;
import dev.profitsoft.intern.lec911.dto.book.BookQueryDto;
import dev.profitsoft.intern.lec911.dto.book.BookSaveDto;

import java.util.List;

public interface BookService {

    //dto in future
    long save(BookSaveDto dto);

    BookDetailsDto findById(long id);

    List<BookDetailsDto> findAll();

    void update(long id, BookSaveDto dto);

    void deleteById(long id);

    List<BookDetailsDto> search(BookQueryDto dto);

}
