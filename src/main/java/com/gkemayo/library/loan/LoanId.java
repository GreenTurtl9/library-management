package com.gkemayo.library.loan;

import com.gkemayo.library.book.Book;
import com.gkemayo.library.customer.Customer;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.time.LocalDateTime;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class LoanId implements Serializable {


    private static final long serialVersionUID = 3912193101593832821L;

    @ManyToOne
    private Book book;

    @ManyToOne
    private Customer customer;

    @Column(name = "CREATION_DATE_TIME")
    private LocalDateTime creationDateTime;
}
