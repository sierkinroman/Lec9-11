package dev.profitsoft.intern.lec911.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Book {

    private long id;

    private String title;

    private String isbn;

    private LocalDate publishedDate;

    private Author author;

}
