package com.ilias.library.service;

import com.ilias.library.entity.Book;
import com.ilias.library.entity.User;
import com.ilias.library.repository.BookRepository;
import com.ilias.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MemberService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private UserRepository userRepository;

    // Get all books
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    public List<Book> searchBooks(String query, String genre, String authorName) {
        return bookRepository.searchBooks(query, genre, authorName);
    }
    public boolean hasBorrowedBook(Long userId) {
        User user = userRepository.findById(Math.toIntExact(userId)).orElse(null);
        return user != null && !user.getBorrowedBooks().isEmpty();
    }

    public void borrowBook(Long bookId, Long userId) {
        User user = userRepository.findById(Math.toIntExact(userId)).orElse(null);
        Book book = bookRepository.findById(bookId).orElse(null);

        if (user != null && book != null && book.getCopiesAvailable() > 0) {
            book.setCopiesAvailable(book.getCopiesAvailable() - 1);
            user.getBorrowedBooks().add(book);
            userRepository.save(user);
            bookRepository.save(book);
            System.out.println("Book borrowed successfully.");
        } else {
            System.out.println("Unable to borrow book. Check availability or user credentials.");
        }
    }

    public void returnBook(Long bookId, Long userId) {
        User user = userRepository.findById(Math.toIntExact(userId)).orElse(null);
        Book book = bookRepository.findById(bookId).orElse(null);

        if (user != null && book != null && user.getBorrowedBooks().contains(book)) {
            book.setCopiesAvailable(book.getCopiesAvailable() + 1);
            user.getBorrowedBooks().remove(book);
            userRepository.save(user);
            bookRepository.save(book);
            System.out.println("Book returned successfully.");
        } else {
            System.out.println("Unable to return book. Check user or book details.");
        }
    }

    public List<Book> getBorrowedBooksByUser(Long userId) {
        User user = userRepository.findById(Math.toIntExact(userId)).orElse(null);
        if (user != null) {
            return new ArrayList<>(user.getBorrowedBooks());
        }
        return new ArrayList<>();
    }
}
