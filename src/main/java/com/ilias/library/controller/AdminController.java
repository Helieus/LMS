package com.ilias.library.controller;

import com.ilias.library.entity.Author;
import com.ilias.library.entity.Book;
import com.ilias.library.service.AdminService;
import com.ilias.library.service.AuthorService;
import com.ilias.library.service.BookService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private BookService bookService;
    @Autowired
    private AuthorService authorService;
    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("books", adminService.getAllBooks());
        model.addAttribute("borrowedBooks", adminService.getBorrowedBooks());
        return "admin-dashboard";
    }

    @GetMapping("/books/new")
    public String addBookForm(Model model) {
        model.addAttribute("book", new Book());
        return "admin/book-form";
    }
    @GetMapping("/books/book-crud")
    public String showBookForm(Model model) {
        model.addAttribute("book", new Book()); // For adding a new book
        model.addAttribute("books", bookService.getAllBooks()); // List of all existing books
        model.addAttribute("authors", authorService.getAllAuthors()); // List of all authors
        return "admin/book-crud";
    }
    @GetMapping("/books/edit/{id}")
    public String editBook(@PathVariable Long id, Model model) {
        Book existingBook = bookService.getBookById(id);
        model.addAttribute("book", existingBook);
        model.addAttribute("books", bookService.getAllBooks());
        model.addAttribute("authors", authorService.getAllAuthors());
        return "admin/book-crud";
    }
    @GetMapping("/books/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return "redirect:/admin/books/book-crud";
    }
    @PostMapping("/books/save")
    public String saveBook(@ModelAttribute Book book) {
        adminService.saveBook(book);
        return "redirect:book-crud"; // Ensure /admin/dashboard exists
    }

    @GetMapping("/book-list")
    public String showBookList(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        return "admin/book-list";
    }

    @PostMapping("/notify/{userId}")
    public String notifyUser(@PathVariable Long userId) {
        adminService.notifyUserToReturnBook(userId);
        return "redirect:/admin/dashboard";
    }
    @GetMapping("/author-manage")
    public String authorManage(Model model) {
        model.addAttribute("authors", authorService.getAllAuthors());
        model.addAttribute("author", new Author());
        return "admin/author-manage";
    }

    @PostMapping("/authors/add")
    public String addAuthor(@ModelAttribute Author author) {
        authorService.saveAuthor(author);
        return "redirect:/admin/author-manage";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        if (session != null) {
            session.invalidate(); // Terminate the session
        }
        return "redirect:/"; // Redirect to the login page
    }

    @GetMapping("/export")
    public String showExportPage() {
        return "admin/export"; // Renders the export.html view
    }


    @GetMapping("/export/pdf")
    public ResponseEntity<InputStreamResource> exportToPDF() {
        ByteArrayInputStream pdfStream = adminService.exportToPdf();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=Books_Report.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(pdfStream));
    }

    @GetMapping("/export/excel")
    public ResponseEntity<InputStreamResource> exportToExcel() {
        ByteArrayInputStream excelStream = adminService.exportToExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=Books_Report.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(excelStream));
    }
}
