package com.airline.Controller;

import com.airline.dto.ReservationHistoryDto;
import com.airline.entity.Airplane;
import com.airline.entity.Cancel;
import com.airline.entity.Customer;
import com.airline.entity.Reserve;
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
import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HistoryController {

  private final ReserveRepository reserveRepository;
  private final CancelRepository cancelRepository;

  @GetMapping("/history")
  public String historyPage(HttpSession session,
      @RequestParam(required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
      @RequestParam(required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
      @RequestParam(defaultValue = "all") String type,
      Model model) {

    Customer customer = (Customer) session.getAttribute("customer");
    if (customer == null) {
      return "redirect:/customers/login";
    }

    // 날짜 없을 경우 경고 메시지 처리
    if (from == null || to == null) {
      model.addAttribute("warningMessage", "시작일시와 종료일시를 입력해주세요.");
      model.addAttribute("results", new ArrayList<>());
      return "history";
    }

    List<Reserve> reserves = List.of();
    List<Cancel> cancels = List.of();

    if ("reserve".equals(type) || "all".equals(type)) {
      reserves = reserveRepository.findByCustomerAndReserveDateTimeBetween(customer, from, to);
    }
    if ("cancel".equals(type) || "all".equals(type)) {
      cancels = cancelRepository.findByCustomerAndCancelDateTimeBetween(customer, from, to);
    }

    List<ReservationHistoryDto> results = new ArrayList<>();

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

    results.sort(Comparator.comparing(ReservationHistoryDto::getProcessedAt).reversed());

    model.addAttribute("results", results);
    model.addAttribute("from", from);
    model.addAttribute("to", to);
    model.addAttribute("type", type);

    return "history";
  }
}
