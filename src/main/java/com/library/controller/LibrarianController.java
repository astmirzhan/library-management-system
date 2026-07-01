package com.library.controller;

import com.library.model.Book;
import com.library.model.BorrowRecord;
import com.library.model.User;
import com.library.service.BookService;
import com.library.service.BorrowService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;

/**
 * Controller for librarian operations: manage books, approve borrows.
 */
@Controller
@RequestMapping("/librarian")
public class LibrarianController {

    private static final Logger logger = LogManager.getLogger(LibrarianController.class);
    private static final int PAGE_SIZE = 20;

    private final BookService bookService;
    private final BorrowService borrowService;

    @Autowired
    public LibrarianController(BookService bookService, BorrowService borrowService) {
        this.bookService = bookService;
        this.borrowService = borrowService;
    }

    /**
     * Librarian dashboard with overview.
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        try {
            List<BorrowRecord> pending = borrowService.getPendingRequests();
            List<BorrowRecord> overdue = borrowService.getOverdueRecords();
            int totalBooks = bookService.getBookCount();

            model.addAttribute("pendingCount", pending.size());
            model.addAttribute("overdueCount", overdue.size());
            model.addAttribute("totalBooks", totalBooks);
            model.addAttribute("pendingRequests", pending);
            model.addAttribute("user", session.getAttribute("currentUser"));

            return "librarian/dashboard";
        } catch (SQLException e) {
            logger.error("Failed to load librarian dashboard", e);
            return "error";
        }
    }

    /**
     * Shows all books for management.
     */
    @GetMapping("/books")
    public String books(@RequestParam(defaultValue = "1") int page,
                        Model model, HttpSession session) {
        try {
            List<Book> books = bookService.getAllBooks(page, PAGE_SIZE);
            model.addAttribute("books", books);
            model.addAttribute("currentPage", page);
            model.addAttribute("user", session.getAttribute("currentUser"));
            return "librarian/books";
        } catch (SQLException e) {
            logger.error("Failed to load books", e);
            return "error";
        }
    }

    /**
     * Shows the form to add a new book.
     */
    @GetMapping("/books/new")
    public String addBookForm(Model model, HttpSession session) {
        try {
            model.addAttribute("authors", bookService.getAllAuthors());
            model.addAttribute("genres", bookService.getAllGenres());
            model.addAttribute("user", session.getAttribute("currentUser"));
            return "librarian/book-form";
        } catch (SQLException e) {
            logger.error("Failed to load add-book form", e);
            return "error";
        }
    }

    /**
     * Creates a new book with the requested number of copies.
     */
    @PostMapping("/books")
    public String createBook(@RequestParam String title,
                             @RequestParam String isbn,
                             @RequestParam int publicationYear,
                             @RequestParam(defaultValue = "1") int copies,
                             @RequestParam(required = false) String description,
                             @RequestParam(required = false) List<Integer> authorIds,
                             @RequestParam(required = false) List<Integer> genreIds,
                             RedirectAttributes redirectAttributes) {
        try {
            Book book = new Book();
            book.setTitle(title);
            book.setIsbn(isbn);
            book.setPublicationYear(publicationYear);
            book.setDescription(description);
            bookService.createBook(book, authorIds, genreIds, copies);
            redirectAttributes.addFlashAttribute("success", "Book added with " + copies + " copies");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/librarian/books/new";
        } catch (SQLException e) {
            logger.error("Failed to create book", e);
            redirectAttributes.addFlashAttribute("error", "System error. Please try again.");
            return "redirect:/librarian/books/new";
        }
        return "redirect:/librarian/books";
    }

    /**
     * Adds more physical copies to an existing book.
     */
    @PostMapping("/books/{bookId}/copies")
    public String addCopies(@PathVariable int bookId,
                            @RequestParam(defaultValue = "1") int count,
                            RedirectAttributes redirectAttributes) {
        try {
            bookService.addCopies(bookId, count);
            redirectAttributes.addFlashAttribute("success", count + " copies added");
        } catch (Exception e) {
            logger.error("Failed to add copies to book {}", bookId, e);
            redirectAttributes.addFlashAttribute("error", "Failed to add copies");
        }
        return "redirect:/librarian/books";
    }

    /**
     * Shows all overdue borrow records (not returned, past due date).
     */
    @GetMapping("/overdue")
    public String overdue(Model model, HttpSession session) {
        try {
            List<BorrowRecord> overdue = borrowService.getOverdueRecords();
            model.addAttribute("overdue", overdue);
            model.addAttribute("user", session.getAttribute("currentUser"));
            return "librarian/overdue";
        } catch (SQLException e) {
            logger.error("Failed to load overdue list", e);
            return "error";
        }
    }

    /**
     * Sends an overdue reminder for a borrow record (demo: logged + confirmation flash).
     */
    @PostMapping("/overdue/remind/{borrowId}")
    public String sendReminder(@PathVariable int borrowId,
                               RedirectAttributes redirectAttributes) {
        logger.info("Overdue reminder sent for borrow record {}", borrowId);
        redirectAttributes.addFlashAttribute("success", "Reminder sent for borrow #" + borrowId);
        return "redirect:/librarian/overdue";
    }

    /**
     * Shows all borrow records for management.
     */
    @GetMapping("/borrows")
    public String borrows(@RequestParam(defaultValue = "1") int page,
                          Model model, HttpSession session) {
        try {
            List<BorrowRecord> borrows = borrowService.getAllBorrows(page, PAGE_SIZE);
            model.addAttribute("borrows", borrows);
            model.addAttribute("currentPage", page);
            model.addAttribute("user", session.getAttribute("currentUser"));
            return "librarian/borrows";
        } catch (SQLException e) {
            logger.error("Failed to load borrows", e);
            return "error";
        }
    }

    /**
     * Approves a pending borrow request.
     */
    @PostMapping("/borrows/approve/{borrowId}")
    public String approveBorrow(@PathVariable int borrowId,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User librarian = (User) session.getAttribute("currentUser");
        try {
            boolean approved = borrowService.approveBorrow(borrowId, librarian.getUserId());
            if (approved) {
                redirectAttributes.addFlashAttribute("success", "Borrow approved");
            } else {
                redirectAttributes.addFlashAttribute("error", "Failed to approve borrow");
            }
        } catch (Exception e) {
            logger.error("Failed to approve borrow {}", borrowId, e);
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/librarian/dashboard";
    }
}