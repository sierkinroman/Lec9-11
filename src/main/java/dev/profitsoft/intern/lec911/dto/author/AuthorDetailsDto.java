package dev.profitsoft.intern.lec911.dto.author;

import dev.profitsoft.intern.lec911.dto.book.BookInfoDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AuthorDetailsDto {

    private String firstName;

    private String lastName;

    private List<BookInfoDto> books;

}
