package com.library.controller;

import com.library.model.User;
import com.library.service.ReviewService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;

/**
 * Controller for submitting book reviews.
 * Business rule (enforced in ReviewService): a user may only review a book they have borrowed.
 */
@Controller
@RequestMapping("/reviews")
public class ReviewController {

    private static final Logger logger = LogManager.getLogger(ReviewController.class);

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * Creates a review for a book, then redirects back to the book details page.
     */
    @PostMapping
    public String create(@RequestParam int bookId,
                         @RequestParam int rating,
                         @RequestParam(required = false) String comment,
                         HttpSession session,
                         RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("currentUser");
        try {
            reviewService.createReview(user.getUserId(), bookId, rating, comment);
            redirectAttributes.addFlashAttribute("success", "Review submitted. Thank you!");
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (SQLException e) {
            logger.error("Failed to create review for book {}", bookId, e);
            redirectAttributes.addFlashAttribute("error", "System error. Please try again.");
        }
        return "redirect:/catalog/" + bookId;
    }
}
