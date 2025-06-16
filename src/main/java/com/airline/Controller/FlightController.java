package com.airline.Controller;

import com.airline.dto.FlightSearchResultDto;
import com.airline.entity.Airplane;
import com.airline.entity.Seats;
import com.airline.repository.AirplaneRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
public class FlightController {

  private final AirplaneRepository airplaneRepository;

  @GetMapping("/flights/search")
  public String searchFlights(
      @RequestParam(required = false) String departureAirport,
      @RequestParam(required = false) String arrivalAirport,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
      @RequestParam(required = false) String seatGrade,
      @RequestParam(defaultValue = "price") String sortField,
      Model model,
      HttpSession session
  ) {
    if (session.getAttribute("customer") == null) {
      return "redirect:/customers/login";
    }

    if (departureAirport == null || arrivalAirport == null || date == null || seatGrade == null) {
      return "search";
    }

    List<Airplane> airplanes = airplaneRepository.findByDepartureAirportAndArrivalAirportAndDate(
        departureAirport, arrivalAirport, date
    );

    List<FlightSearchResultDto> flights = airplanes.stream()
        .map(airplane -> {
          Seats matchedSeat = airplane.getSeats().stream()
              .filter(seat -> seat.getSeatClass().equalsIgnoreCase(seatGrade))
              .findFirst()
              .orElse(null);
          if (matchedSeat == null) return null;

          return new FlightSearchResultDto(
              airplane.getId(),
              airplane.getAirline(),
              airplane.getFlightNo(),
              airplane.getDepartureAirport(),
              airplane.getArrivalAirport(),
              airplane.getDepartureDateTime(),
              airplane.getArrivalDateTime(),
              matchedSeat.getPrice(),
              matchedSeat.getNoOfSeats()
          );
        })
        .filter(Objects::nonNull)
        .sorted((f1, f2) -> {
          switch (sortField) {
            case "departureDateTime":
              return f1.getDepartureDateTime().compareTo(f2.getDepartureDateTime());
            case "arrivalDateTime":
              return f1.getArrivalDateTime().compareTo(f2.getArrivalDateTime());
            case "price":
            default:
              return Integer.compare(f1.getPrice(), f2.getPrice());
          }
        })
        .toList();

    model.addAttribute("flights", flights);
    model.addAttribute("seatGrade", seatGrade);
    model.addAttribute("sortField", sortField);
    return "search";
  }
}
