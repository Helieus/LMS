package com.ilias.library.service;

import com.ilias.library.entity.Book;
import com.ilias.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    public List<Book> searchBooks(String query, String genre, String authorName) {
        return bookRepository.searchBooks(query, genre, authorName);
    }
    public void saveBook(Book book) {
        bookRepository.save(book);
    }
    public Book getBookById(Long id) {
        return bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book not found"));
    }

    public void saveBook(Book book, MultipartFile coverImage) {
        // Handle cover image storage if needed
        bookRepository.save(book);
    }

    public void updateBook(Long id, Book updatedBook, MultipartFile coverImage) {
        Book existingBook = getBookById(id);
        existingBook.setTitle(updatedBook.getTitle());
        existingBook.setIsbn(updatedBook.getIsbn());
        existingBook.setGenre(updatedBook.getGenre());
        existingBook.setPublicationDate(updatedBook.getPublicationDate());
        existingBook.setCopiesAvailable(updatedBook.getCopiesAvailable());
        existingBook.setSummary(updatedBook.getSummary());
        // Handle cover image update if needed
        bookRepository.save(existingBook);
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
}

