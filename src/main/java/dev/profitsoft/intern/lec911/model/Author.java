package dev.profitsoft.intern.lec911.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class Author {

    private long id;

    private String firstName;

    private String lastName;

    private Set<Book> books = new HashSet<>();

}
