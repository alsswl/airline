package com.airline.repository;

import com.airline.entity.Cancel;
import com.airline.entity.Customer;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CancelRepository extends JpaRepository<Cancel, Long> {
  List<Cancel> findByCustomerCno(String cno);
  List<Cancel> findByCustomerAndCancelDateTimeBetween(Customer customer, LocalDateTime from, LocalDateTime to);
}

