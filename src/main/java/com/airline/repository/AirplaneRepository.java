package com.airline.repository;

import com.airline.entity.Airplane;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AirplaneRepository extends JpaRepository<Airplane, Long> {

  @Query("SELECT a FROM Airplane a WHERE a.departureAirport = :departure AND"
      + " a.arrivalAirport = :arrival AND DATE(a.departureDateTime) = :date")
  List<Airplane> findByDepartureAirportAndArrivalAirportAndDate(
      String departure, String arrival, LocalDate date);

}

