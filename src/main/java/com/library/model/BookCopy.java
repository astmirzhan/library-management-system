package com.library.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a physical copy of a book.
 * Each book can have multiple copies, each with its own condition and status.
 */
public class BookCopy {

    public enum Condition {
        NEW, GOOD, WORN, DAMAGED
    }

    private int copyId;
    private int bookId;
    private int copyNumber;
    private Condition condition;
    private LocalDate acquisitionDate;

    private Book book;

    public BookCopy() {
    }

    public BookCopy(int copyId, int bookId, int copyNumber, Condition condition,
                    LocalDate acquisitionDate) {
        this.copyId = copyId;
        this.bookId = bookId;
        this.copyNumber = copyNumber;
        this.condition = condition;
        this.acquisitionDate = acquisitionDate;
    }

    public int getCopyId() {
        return copyId;
    }

    public void setCopyId(int copyId) {
        this.copyId = copyId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getCopyNumber() {
        return copyNumber;
    }

    public void setCopyNumber(int copyNumber) {
        this.copyNumber = copyNumber;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public LocalDate getAcquisitionDate() {
        return acquisitionDate;
    }

    public void setAcquisitionDate(LocalDate acquisitionDate) {
        this.acquisitionDate = acquisitionDate;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookCopy bookCopy = (BookCopy) o;
        return copyId == bookCopy.copyId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(copyId);
    }

    @Override
    public String toString() {
        return "BookCopy{" +
                "copyId=" + copyId +
                ", bookId=" + bookId +
                ", copyNumber=" + copyNumber +
                ", condition=" + condition +
                '}';
    }
}