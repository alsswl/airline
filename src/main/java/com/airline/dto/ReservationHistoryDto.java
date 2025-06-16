package com.airline.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReservationHistoryDto {
  private String type; // 예약 or 취소
  private String airline;
  private String flightNo;
  private String departureAirport;
  private String arrivalAirport;
  private LocalDateTime departureDateTime;
  private LocalDateTime arrivalDateTime;
  private int amount;
  private LocalDateTime processedAt;
}
