package com.airline.repository;

import com.airline.entity.Airplane;
import com.airline.entity.Customer;
import com.airline.entity.Reserve;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReserveRepository extends JpaRepository<Reserve, Long> {
  List<Reserve> findByCustomerCno(String cno);
  List<Reserve> findByCustomerAndReserveDateTimeBetween(Customer customer, LocalDateTime from, LocalDateTime to);
  boolean existsByCustomerAndAirplane(Customer customer, Airplane airplane);
  @Query("SELECT r.airplane.flightNo, COUNT(r) FROM Reserve r GROUP BY r.airplane.flightNo")
  List<Object[]> countByAirplane();

  @Query("SELECT r.seatClass, SUM(r.payment) FROM Reserve r GROUP BY r.seatClass")
  List<Object[]> sumBySeatClass();

  @Query("SELECT FUNCTION('DATE_FORMAT', r.reserveDateTime, '%Y-%m'), SUM(r.payment) FROM Reserve r GROUP BY FUNCTION('DATE_FORMAT', r.reserveDateTime, '%Y-%m')")
  List<Object[]> monthlyRevenue();

  @Query("SELECT SUM(r.payment) FROM Reserve r")
  Integer totalPayment();

  @Query("SELECT r.customer.name, COUNT(r) FROM Reserve r GROUP BY r.customer.name")
  List<Object[]> customerReserveCount();

  @Query(value = """
  SELECT r.customer_cno,
         c.name,
         r.reserve_date_time,
         COUNT(*) OVER (PARTITION BY r.customer_cno ORDER BY r.reserve_date_time) AS cumulative_count
  FROM reserve r
  JOIN customer c ON r.customer_cno = c.cno
  ORDER BY r.customer_cno, r.reserve_date_time
""", nativeQuery = true)
  List<Object[]> findCumulativeReservations();



}

