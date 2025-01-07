package com.ilias.library.controller;

import com.ilias.library.entity.Book;
import com.ilias.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/admin/book")
public class BookController {

    @Autowired
    private BookService bookService;
    @PostMapping("/add")
    public String addBook(@ModelAttribute Book book) {
        bookService.saveBook(book);
        // For debugging, print out the book details here
        System.out.println("Book saved: " + book);
        return "admin/book-form";
    }



    @GetMapping
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        return "admin/book-list"; // Create admin/book-list.html
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("book", new Book());
        return "admin/book-form"; // Create admin/book-form.html
    }

   /* @PostMapping("/add")
    public String addBook(@ModelAttribute Book book,
                          @RequestParam("coverImage") MultipartFile coverImage) {
        bookService.saveBook(book, coverImage);
        return "redirect:/admin/book";
    }*/

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("book", bookService.getBookById(id));
        return "admin/book-form";
    }

    @PostMapping("/edit/{id}")
    public String editBook(@PathVariable Long id, @ModelAttribute Book book,
                           @RequestParam("coverImage") MultipartFile coverImage) {
        bookService.updateBook(id, book, coverImage);
        return "redirect:/admin/book";
    }

    @PostMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return "redirect:/admin/book";
    }
}
