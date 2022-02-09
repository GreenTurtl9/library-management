package com.gkemayo.library.loan;

import com.gkemayo.library.book.Book;
import com.gkemayo.library.customer.Customer;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.time.LocalDateTime;

@Embeddable
public class LoanId implements Serializable {


    private static final long serialVersionUID = 3912193101593832821L;

    private Book book;

    private Customer customer;

    private LocalDateTime creationDateTime;

    public LoanId() {
        super();
    }

    public LoanId(Book book, Customer customer) {
        super();
        this.book = book;
        this.customer = customer;
        this.creationDateTime = LocalDateTime.now();
    }

    @ManyToOne
    public Book getBook() {
        return book;
    }

    public void setBook(Book bbok) {
        this.book = bbok;
    }

    @ManyToOne
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Column(name = "CREATION_DATE_TIME")
    public LocalDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(LocalDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
    }
}
