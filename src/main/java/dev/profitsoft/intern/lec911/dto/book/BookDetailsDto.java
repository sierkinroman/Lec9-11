package dev.profitsoft.intern.lec911.dto.book;

import dev.profitsoft.intern.lec911.dto.author.AuthorInfoDto;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;

@Getter
@Builder
@Jacksonized
public class BookDetailsDto {

    private String title;

    private String isbn;

    private LocalDate publishedDate;

    private AuthorInfoDto author;

}
