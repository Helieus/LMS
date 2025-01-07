package com.ilias.library.controller;

import com.ilias.library.entity.Book;
import com.ilias.library.entity.User;
import com.ilias.library.service.AuthorService;
import com.ilias.library.service.MemberService;
import com.ilias.library.specification.BookSpecification;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.ilias.library.repository.BookRepository;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
@Controller
@RequestMapping("/member")
public class MemberController {

    @Autowired
    private MemberService memberService;
    @Autowired
    private BookRepository bookRepository; // Inject the BookRepository bean
    @Autowired
    private AuthorService authorService;

    @GetMapping("/search-results")
    public String searchResultsPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/";
        List<Book> books = bookRepository.findAll();
        model.addAttribute("user", user);
        model.addAttribute("books", memberService.getAllBooks());
        model.addAttribute("borrowedBooks", memberService.getBorrowedBooksByUser(user.getId()));

        return "member/search-results"; // Points to search-results.html
    }
    // Borrowed books page
    @GetMapping("/borrowed-books")
    public String borrowedBooksPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) return "redirect:/";

        model.addAttribute("borrowedBooks", memberService.getBorrowedBooksByUser(user.getId()));
        return "member/borrowed-books"; // Points to borrowed-books.html
    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        if (session != null) {
            session.invalidate(); // Terminate the session
        }
        return "redirect:/"; // Redirect to the login page
    }
    @GetMapping("/borrow/{id}")
    public String borrowBook(@PathVariable Long id, HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user != null) {
            memberService.borrowBook(id, user.getId());

            // Calculate the due date
            LocalDate today = LocalDate.now();
            LocalDate dueDate = today.plusDays(5);

            // Format the date for display
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
            String dueDateFormatted = dueDate.format(formatter);

            // Add the borrow message with the calculated date
            model.addAttribute("borrowMessage", "You have until " + dueDateFormatted + " to return the book.");


        }
        return "redirect:/member/search-results";
    }

    @GetMapping("/return/{id}")
    public String returnBook(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user != null) {
            memberService.returnBook(id, user.getId());

        }
        return "redirect:/member/search-results";
    }

    @GetMapping("/borrowed-books-data")
    @ResponseBody
    public List<Book> getBorrowedBooksData(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String genre) {

        return bookRepository.findAll((root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            if (title != null && !title.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
            }
            if (author != null && !author.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.join("author").get("name")), "%" + author.toLowerCase() + "%"));
            }
            if (genre != null && !genre.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("genre")), "%" + genre.toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        });
    }
}
