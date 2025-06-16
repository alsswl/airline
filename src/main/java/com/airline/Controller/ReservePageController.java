package com.airline.Controller;

import com.airline.dto.ReservationHistoryDto;
import com.airline.entity.*;
import com.airline.repository.CancelRepository;
import com.airline.repository.ReserveRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ReservePageController {

  private final ReserveRepository reserveRepository;
  private final CancelRepository cancelRepository;

  @GetMapping("/my/history")
  public String viewHistory(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
      @RequestParam(defaultValue = "all") String type,
      HttpSession session,
      Model model) {

    Customer customer = (Customer) session.getAttribute("customer");
    if (customer == null) {
      return "redirect:/customers/login";
    }

    if (from == null || to == null) {
      model.addAttribute("warningMessage", "조회 시작일시와 종료일시를 모두 입력해주세요.");
      return "my-history";
    }

    List<ReservationHistoryDto> results = new ArrayList<>();

    if (!"cancel".equals(type)) {
      List<Reserve> reserves = reserveRepository.findByCustomerAndReserveDateTimeBetween(customer, from, to);
      for (Reserve r : reserves) {
        Airplane a = r.getAirplane();
        results.add(new ReservationHistoryDto(
            "예약",
            a.getAirline(),
            a.getFlightNo(),
            a.getDepartureAirport(),
            a.getArrivalAirport(),
            a.getDepartureDateTime(),
            a.getArrivalDateTime(),
            r.getPayment(),
            r.getReserveDateTime()
        ));
      }
    }

    if (!"reserve".equals(type)) {
      List<Cancel> cancels = cancelRepository.findByCustomerAndCancelDateTimeBetween(customer, from, to);
      for (Cancel c : cancels) {
        Airplane a = c.getAirplane();
        results.add(new ReservationHistoryDto(
            "취소",
            a.getAirline(),
            a.getFlightNo(),
            a.getDepartureAirport(),
            a.getArrivalAirport(),
            a.getDepartureDateTime(),
            a.getArrivalDateTime(),
            c.getRefund(),
            c.getCancelDateTime()
        ));
      }
    }

    model.addAttribute("results", results);
    model.addAttribute("from", from);
    model.addAttribute("to", to);
    model.addAttribute("type", type);
    return "my-history";
  }
}
