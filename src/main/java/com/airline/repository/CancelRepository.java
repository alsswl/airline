package com.airline.repository;

import com.airline.entity.Cancel;
import com.airline.entity.Customer;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CancelRepository extends JpaRepository<Cancel, Long> {
  List<Cancel> findByCustomerCno(String cno);
  List<Cancel> findByCustomerAndCancelDateTimeBetween(Customer customer, LocalDateTime from, LocalDateTime to);
  @Query("SELECT c.airplane.flightNo, COUNT(c) FROM Cancel c GROUP BY c.airplane.flightNo")
  List<Object[]> countByAirplane();

  @Query("SELECT SUM(c.refund) FROM Cancel c")
  Integer totalRefund();
}

