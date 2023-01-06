package dev.profitsoft.intern.lec911.dto.book;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
public class BookInfoDto {

    private String title;

    private LocalDate publishedDate;

}
