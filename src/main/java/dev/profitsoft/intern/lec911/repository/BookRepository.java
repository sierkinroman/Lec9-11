package dev.profitsoft.intern.lec911.repository;

import dev.profitsoft.intern.lec911.model.Author;
import dev.profitsoft.intern.lec911.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(String isbn);

    @Query("SELECT b FROM Book b WHERE b.author = :author OR YEAR(b.publishedDate) = :year")
    Page<Book> searchAllByAuthorOrYear(@Param("author") Author author, @Param("year") Integer year, Pageable pageable);

}
