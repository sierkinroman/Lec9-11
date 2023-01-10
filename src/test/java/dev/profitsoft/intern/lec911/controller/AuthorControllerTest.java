package dev.profitsoft.intern.lec911.controller;

import dev.profitsoft.intern.lec911.Lec911Application;
import dev.profitsoft.intern.lec911.model.Author;
import dev.profitsoft.intern.lec911.model.Book;
import dev.profitsoft.intern.lec911.repository.AuthorRepository;
import dev.profitsoft.intern.lec911.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = Lec911Application.class)
@AutoConfigureMockMvc
class AuthorControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @BeforeEach
    public void beforeEach() {
        bookRepository.deleteAll();
        authorRepository.deleteAll();
    }

    @Test
    public void testGetAllAuthors_success() throws Exception {
        Author author1 = createAuthor("Roman", "Romanov");
        Author author2 = createAuthor("Ivan", "Ivanov");
        createBook("book1", "123", author1);
        createBook("book2", "1234", author1);
        createBook("book3", "12345", author2);

        ResultActions response = mvc.perform(get("/api/authors"));

        response
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].books[*].title").exists())
                .andExpect(jsonPath("$[*].books[*].publishedDate").exists());
    }

    private Author createAuthor(String firstName, String lastName) {
        Author author = new Author();
        author.setFirstName(firstName);
        author.setLastName(lastName);
        return authorRepository.save(author);
    }

    private Book createBook(String title, String isbn, Author author) {
        Book book = new Book();
        book.setTitle(title);
        book.setIsbn(isbn);
        book.setPublishedDate(LocalDate.now());
        book.setAuthor(author);
        return bookRepository.save(book);
    }

    @Test
    public void testGetAllAuthors_noContent() throws Exception {
        mvc.perform(get("/api/authors"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());
    }

}