package com.airline.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Airplane {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String flightNo;
  private LocalDateTime departureDateTime;

  private String airline;
  private String departureAirport;
  private String arrivalAirport;
  private LocalDateTime arrivalDateTime;

  @OneToMany(mappedBy = "airplane")
  private List<Seats> seats;

  @OneToMany(mappedBy = "airplane")
  private List<Reserve> reserves;

  @OneToMany(mappedBy = "airplane")
  private List<Cancel> cancels;
}


