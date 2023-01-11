package dev.profitsoft.intern.lec911.dto.book;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import dev.profitsoft.intern.lec911.dto.author.AuthorInfoDto;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class BookDetailsDto {

    @JsonUnwrapped
    private BookInfoDto bookInfo;

    private String isbn;

    private AuthorInfoDto author;

}
