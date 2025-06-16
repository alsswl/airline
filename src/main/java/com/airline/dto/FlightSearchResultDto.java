package com.airline.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class FlightSearchResultDto {
    private Long airplaneId;
    private String airline;
    private String flightNo;
    private String departureAirport;
    private String arrivalAirport;
    private LocalDateTime departureDateTime;
    private LocalDateTime arrivalDateTime;
    private int price;
    private int noOfSeats;
}
