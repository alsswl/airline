package com.airline.repository;

import com.airline.entity.Seats;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatsRepository extends JpaRepository<Seats, Long> {
  List<Seats> findByAirplaneId(Long airplaneId);
  Optional<Seats> findByAirplaneIdAndSeatClass(Long airplaneId, String seatClass);
}

