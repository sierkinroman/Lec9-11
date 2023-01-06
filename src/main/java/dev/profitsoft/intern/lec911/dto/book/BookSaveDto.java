package dev.profitsoft.intern.lec911.dto.book;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Getter
@Builder
@Jacksonized
public class BookSaveDto {

    @NotBlank(message = "title is required")
    private String title;

    @NotBlank(message = "isbn is required")
    private String isbn;

    private LocalDate publishedDate;

    @Positive(message = "authorId must be positive number")
    private Long authorId;

}
