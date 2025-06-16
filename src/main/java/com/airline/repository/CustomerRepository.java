package com.airline.repository;

import com.airline.entity.Customer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, String> {
  Optional<Customer> findByEmail(String email);
}
