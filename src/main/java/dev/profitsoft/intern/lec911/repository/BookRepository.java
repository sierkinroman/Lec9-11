package dev.profitsoft.intern.lec911.repository;

import dev.profitsoft.intern.lec911.model.Author;
import dev.profitsoft.intern.lec911.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    Page<Book> findAllByAuthorAndPublishedDateBetween(Author author, LocalDate dateStart, LocalDate dateEnd, Pageable pageable);

    Page<Book> findAllByAuthor(Author author, Pageable pageable);

    Page<Book> findAllByPublishedDateBetween(LocalDate dateStart, LocalDate dateEnd, Pageable pageable);

}
