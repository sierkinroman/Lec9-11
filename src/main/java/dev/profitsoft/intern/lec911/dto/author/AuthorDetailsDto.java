package dev.profitsoft.intern.lec911.dto.author;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import dev.profitsoft.intern.lec911.dto.book.BookInfoDto;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Getter
@Jacksonized
@Builder
public class AuthorDetailsDto {

    @JsonUnwrapped
    private AuthorInfoDto author;

    private List<BookInfoDto> books;

}
