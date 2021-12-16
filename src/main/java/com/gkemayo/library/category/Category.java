package com.gkemayo.library.category;

import com.gkemayo.library.book.Book;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "CATEGORY")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id
    @Column(name = "CODE")
    private String code;
    private String label;

    @OneToMany(mappedBy = "category")
    private Set<Book> books;
}



