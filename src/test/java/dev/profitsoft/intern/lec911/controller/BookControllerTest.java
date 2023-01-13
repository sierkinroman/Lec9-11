package dev.profitsoft.intern.lec911.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.profitsoft.intern.lec911.Lec911Application;
import dev.profitsoft.intern.lec911.dto.RestResponse;
import dev.profitsoft.intern.lec911.dto.book.BookSaveDto;
import dev.profitsoft.intern.lec911.dto.book.BookSearchDto;
import dev.profitsoft.intern.lec911.model.Author;
import dev.profitsoft.intern.lec911.model.Book;
import dev.profitsoft.intern.lec911.repository.AuthorRepository;
import dev.profitsoft.intern.lec911.repository.BookRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = Lec911Application.class)
@AutoConfigureMockMvc
@Sql(scripts = "classpath:clear-tables.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class BookControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @AfterEach
    public void beforeEach() {
        bookRepository.deleteAll();
        authorRepository.deleteAll();
    }

    @Test
    public void testCreateBook_success() throws Exception {
        Author author = createAuthor("Roman", "Romanov");
        BookSaveDto saveDto = getBookSaveDto("New book", "123456", LocalDate.now(), author.getId());

        MvcResult mvcResult = mvc.perform(
                post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveDto))
                )
                .andExpect(status().isCreated())
                .andReturn();

        RestResponse response = parseResponse(mvcResult, RestResponse.class);
        long bookId = Long.parseLong(response.getResult());
        Book book = bookRepository.findById(bookId).orElse(null);

        assertThat(book).isNotNull();
        assertThat(book.getTitle()).isEqualTo(saveDto.getTitle());
        assertThat(book.getIsbn()).isEqualTo(saveDto.getIsbn());
        assertThat(book.getPublishedDate()).isEqualTo(saveDto.getPublishedDate());
        assertThat(book.getAuthor().getId()).isEqualTo(author.getId());
    }

    private Author createAuthor(String firstName, String lastName) {
        Author author = new Author();
        author.setFirstName(firstName);
        author.setLastName(lastName);
        return authorRepository.save(author);
    }

    private BookSaveDto getBookSaveDto(String title, String isbn, LocalDate publishedDate, long authorId) {
        return BookSaveDto.builder()
                .title(title)
                .isbn(isbn)
                .publishedDate(publishedDate)
                .authorId(authorId)
                .build();
    }

    private <T>T parseResponse(MvcResult mvcResult, Class<T> clazz) {
        try {
            return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), clazz);
        } catch (JsonProcessingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Error parsing json", e);
        }
    }

    @Test
    public void testCreateBook_duplicateIsbn() throws Exception {
        Author author = createAuthor("Roman", "Romanov");
        createBook("new book", "12345", author);
        BookSaveDto saveDto = getBookSaveDto("new book2", "12345", LocalDate.now(), author.getId());

        mvc.perform(
                post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertThat(result.getResolvedException().getMessage()).isEqualTo("book with given isbn already exists"));
    }

    @Test
    public void testCreateBook_publishedDateAfterNow() throws Exception {
        Author author = createAuthor("Roman", "Romanov");
        BookSaveDto saveDto = getBookSaveDto("new book", "12345", LocalDate.now().plusDays(1), author.getId());

        mvc.perform(
                post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertThat(result.getResolvedException().getMessage()).isEqualTo("publishedDate should be before now"));
    }

    @Test
    public void testCreateBook_authorIdNotFound() throws Exception {
        BookSaveDto saveDto = getBookSaveDto("new book2", "12345", LocalDate.now(), 100);

        mvc.perform(
                post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertThat(result.getResolvedException().getMessage()).isEqualTo("Author with id %d not found".formatted(100)));
    }

    @Test
    public void testGetBook_success() throws Exception {
        Author author = createAuthor("Roman", "Romanov");
        Book book = createBook("new book", "12345", author);

        ResultActions response = mvc.perform(get("/api/books/{id}", book.getId()));

        response
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(book.getId()))
                .andExpect(jsonPath("$.title").value(book.getTitle()))
                .andExpect(jsonPath("$.isbn").value(book.getIsbn()))
                .andExpect(jsonPath("$.publishedDate").value(book.getPublishedDate().toString()))
                .andExpect(jsonPath("$.author.id").value(book.getAuthor().getId()))
                .andExpect(jsonPath("$.author.firstName").value(book.getAuthor().getFirstName()))
                .andExpect(jsonPath("$.author.lastName").value(book.getAuthor().getLastName()));
    }

    @Test
    public void testGetBook_notFound() throws Exception {
        mvc.perform(get("/api/books/100"))
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertThat(result.getResolvedException().getMessage()).isEqualTo("Book with id %d not found".formatted(100)));
    }
    
    @Test
    public void testGetAllBooks_success() throws Exception {
        Author author1 = createAuthor("Roman", "Romanov");
        Author author2 = createAuthor("Ivan", "Ivanov");
        createBook("book1", "123", author1);
        createBook("book2", "1234", author1);
        createBook("book3", "12345", author2);

        ResultActions response = mvc.perform(get("/api/books/"));

        response
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].author.id").exists())
                .andExpect(jsonPath("$[*].author.firstName").exists())
                .andExpect(jsonPath("$[*].author.lastName").exists());

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
    public void testGetAllBooks_noContent() throws Exception {
        mvc.perform(get("/api/books"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void testUpdateBook_success() throws Exception {
        Author author = createAuthor("Roman", "Romanov");
        Book book = createBook("new book", "12345", author);
        BookSaveDto saveDto = getBookSaveDto("updated Title", "123456", LocalDate.now(), author.getId());

        mvc.perform(
                put("/api/books/{id}", book.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveDto))
                )
                .andExpect(status().isOk());

        Book updatedBook = bookRepository.findById(book.getId()).orElse(null);
        assertThat(updatedBook).isNotNull();
        assertThat(updatedBook.getTitle()).isEqualTo(saveDto.getTitle());
        assertThat(updatedBook.getIsbn()).isEqualTo(saveDto.getIsbn());
        assertThat(updatedBook.getPublishedDate()).isEqualTo(saveDto.getPublishedDate());
        assertThat(updatedBook.getAuthor().getId()).isEqualTo(saveDto.getAuthorId());
    }

    @Test
    public void testUpdateBook_notFound() throws Exception {
        Author author = createAuthor("Roman", "Romanov");
        BookSaveDto saveDto = getBookSaveDto("updated Title", "12345", LocalDate.now(), author.getId());

        mvc.perform(
                put("/api/books/{id}", 100)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveDto))
                )
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertThat(result.getResolvedException().getMessage()).isEqualTo("Book with id %d not found".formatted(100)));
    }

    @Test
    public void testUpdateBook_duplicateIsbn() throws Exception {
        Author author = createAuthor("Roman", "Romanov");
        Book book = createBook("new book", "12345", author);
        BookSaveDto saveDto = getBookSaveDto("updated Title", "12345", LocalDate.now(), author.getId());

        mvc.perform(
                put("/api/books/{id}", book.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertThat(result.getResolvedException().getMessage()).isEqualTo("book with given isbn already exists"));
    }

    @Test
    public void testUpdateBook_publishedDateAfterNow() throws Exception {
        Author author = createAuthor("Roman", "Romanov");
        Book book = createBook("new book", "12345", author);
        BookSaveDto saveDto = getBookSaveDto("updated Title", "123456", LocalDate.now().plusDays(1), author.getId());

        mvc.perform(
                put("/api/books/{id}", book.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertThat(result.getResolvedException().getMessage()).isEqualTo("publishedDate should be before now"));
    }

    @Test
    public void testUpdateBook_authorIdNotFound() throws Exception {
        Author author = createAuthor("Roman", "Romanov");
        Book book = createBook("new book", "12345", author);
        BookSaveDto saveDto = getBookSaveDto("updated Title", "123456", LocalDate.now(), 100);

        mvc.perform(
                put("/api/books/{id}", book.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertThat(result.getResolvedException().getMessage()).isEqualTo("Author with id %d not found".formatted(100)));
    }

    @Test
    @Sql("classpath:data-test.sql")
    public void testSearchBook_success_byYearAndAuthorId() throws Exception {
        BookSearchDto searchDto = new BookSearchDto(1L, 2002, null, null);

        mvc.perform(
                post("/api/books/_search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(7)));
    }

    @Test
    @Sql("classpath:data-test.sql")
    public void testSearchBook_success_byAuthorId() throws Exception{
        BookSearchDto searchDto = new BookSearchDto(1L, null, null, null);

        mvc.perform(
                post("/api/books/_search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[*].author.id", everyItem(equalTo(1))));
    }

    @Test
    @Sql("classpath:data-test.sql")
    public void testSearchBook_success_byYear() throws Exception{
        BookSearchDto searchDto = new BookSearchDto(null, 2002, null, null);

        mvc.perform(
                post("/api/books/_search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[*].publishedDate", everyItem(containsString("2002"))));
    }

    @Test
    @Sql("classpath:data-test.sql")
    public void testSearchBook_success_pagination() throws Exception{
        BookSearchDto searchDto = new BookSearchDto(1L, 2002, 1, 5);

        mvc.perform(
                post("/api/books/_search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchDto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)));
    }

    @Test
    @Sql("classpath:data-test.sql")
    public void testSearchBook_authorIdNotFound() throws Exception{
        BookSearchDto searchDto = new BookSearchDto(100L, 2002, 1, 5);

        mvc.perform(
                post("/api/books/_search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchDto))
                )
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertThat(result.getResolvedException().getMessage()).isEqualTo("Author with id %d not found".formatted(100)));
    }

    @Test
    @Sql("classpath:data-test.sql")
    public void testSearchBook_noContent() throws Exception{
        BookSearchDto searchDto = new BookSearchDto(null, 2002, 11, 5);

        mvc.perform(
                post("/api/books/_search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchDto))
                )
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    public void testDeleteBook_success() throws Exception {
        Author author = createAuthor("Roman", "Romanov");
        Book book = createBook("new book", "12345", author);

        mvc.perform(delete("/api/books/{id}", book.getId()))
                .andExpect(status().isNoContent());

        assertThat(bookRepository.findById(book.getId())).isEmpty();
    }

    @Test
    public void testDeleteBook_notFound() throws Exception {
        mvc.perform(delete("/api/books/{id}", 100))
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertThat(result.getResolvedException().getMessage()).isEqualTo("Book with id %d not found".formatted(100)));
    }

}