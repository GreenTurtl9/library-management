package com.bsaoudi.library.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICustomerDao extends JpaRepository<Customer, Integer> {

    public Customer findCustomerByEmailIgnoreCase(String email);

    public List<Customer> findCustomerByLastNameIgnoreCase(String lastName);
}
