package dev.profitsoft.intern.lec911.repository;

import dev.profitsoft.intern.lec911.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

}
