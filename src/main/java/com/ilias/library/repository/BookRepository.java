package com.ilias.library.repository;

import com.ilias.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    /**
     * Search for books by title, genre, or author name.
     *
     * @param query      Search query for the title (nullable)
     * @param genre      Filter by genre (nullable)
     * @param authorName Filter by author name (nullable)
     * @return List of books matching the search criteria
     */
    @Query("SELECT b FROM Book b WHERE " +
            "(:query IS NULL OR b.title LIKE %:query%) AND " +
            "(:genre IS NULL OR b.genre LIKE %:genre%) AND " +
            "(:authorName IS NULL OR b.author.name LIKE %:authorName%)")
    List<Book> searchBooks(@Param("query") String query,
                           @Param("genre") String genre,
                           @Param("authorName") String authorName);

    /**
     * Find all books currently borrowed by any user.
     *
     * @return List of borrowed books
     */
    @Query("SELECT DISTINCT b FROM Book b JOIN b.borrowers u")
    List<Book> findAllBorrowedBooks();

    /**
     * Find books borrowed by a specific user.
     *
     * @param userId ID of the user
     * @return List of books borrowed by the user
     */
    @Query("SELECT b FROM Book b JOIN b.borrowers u WHERE u.id = :userId")
    List<Book> findBorrowedBooksByUser(@Param("userId") Long userId);


}
