package com.airline.repository;

import com.airline.entity.Airplane;
import com.airline.entity.Customer;
import com.airline.entity.Reserve;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReserveRepository extends JpaRepository<Reserve, Long> {
  List<Reserve> findByCustomerCno(String cno);
  List<Reserve> findByCustomerAndReserveDateTimeBetween(Customer customer, LocalDateTime from, LocalDateTime to);
  boolean existsByCustomerAndAirplane(Customer customer, Airplane airplane);


}

