package dev.profitsoft.intern.lec911.dto.book;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Getter
@Setter
@AllArgsConstructor
public class BookSearchDto {

    private Long authorId;

    @Min(value = 1000, message = "year should have 4 digits")
    @Max(value = 9999, message = "year should have 4 digits")
    private Integer year;

    @Min(value = 1, message = "page number should be positive number")
    private Integer page;

    @Min(value = 1, message = "page size should be positive number")
    private Integer size;

}
