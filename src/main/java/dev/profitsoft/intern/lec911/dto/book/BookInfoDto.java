package dev.profitsoft.intern.lec911.dto.book;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;

@Getter
@Builder
@Jacksonized
@AllArgsConstructor
public class BookInfoDto {

    private long id;

    private String title;

    private LocalDate publishedDate;

}
