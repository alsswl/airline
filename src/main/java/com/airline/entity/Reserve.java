package com.airline.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Entity
@Setter
@Getter
public class Reserve {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "airplane_id")
  private Airplane airplane;

  @ManyToOne
  @JoinColumn(name = "customer_cno")
  private Customer customer;

  private String seatClass;
  private int payment;
  private LocalDateTime reserveDateTime;
}

