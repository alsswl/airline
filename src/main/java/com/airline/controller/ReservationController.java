package com.airline.controller;

import com.airline.entity.*;
import com.airline.repository.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class ReservationController {

  private final AirplaneRepository airplaneRepository;
  private final ReserveRepository reserveRepository;
  private final CancelRepository cancelRepository;
  private final CustomerRepository customerRepository;

  @Autowired
  private JavaMailSender mailSender;

  @PostMapping("/reserve")
  public String reserve(@RequestParam Long airplaneId,
      @RequestParam String seatGrade,
      HttpSession session,
      RedirectAttributes redirectAttributes) {

    Customer customer = (Customer) session.getAttribute("customer");
    if (customer == null) {
      return "redirect:/customers/login";
    }

    Airplane airplane = airplaneRepository.findById(airplaneId)
        .orElseThrow();

    if (reserveRepository.existsByCustomerAndAirplane(customer, airplane)) {
      redirectAttributes.addFlashAttribute("errorMessage", "이미 예약한 항공편입니다.");
      return "redirect:/flights/search";
    }

    Seats seat = airplane.getSeats().stream()
        .filter(s -> s.getSeatClass().equalsIgnoreCase(seatGrade))
        .findFirst()
        .orElseThrow();

    if (seat.getNoOfSeats() <= 0) {
      redirectAttributes.addFlashAttribute("errorMessage", "해당 좌석 등급은 예약이 모두 마감되었습니다.");
      return "redirect:/flights/search";
    }

    seat.setNoOfSeats(seat.getNoOfSeats() - 1);

    Reserve reserve = new Reserve();
    reserve.setCustomer(customer);
    reserve.setAirplane(airplane);
    reserve.setSeatClass(seatGrade);
    reserve.setPayment(seat.getPrice());
    reserve.setReserveDateTime(LocalDateTime.now());

    reserveRepository.save(reserve);
    airplaneRepository.save(airplane);

    // ✅ 이메일 전송
    try {
      SimpleMailMessage message = new SimpleMailMessage();
      message.setTo(customer.getEmail());  // 📌 customer.getEmail()이 null이 아니어야 함
      message.setSubject("[CNU Airline] 예약 완료 안내");

      String flightInfo = customer.getName() + "님,\n\n" +
          "항공편 예약이 완료되었습니다.\n\n" +
          "항공사: " + airplane.getAirline() + "\n" +
          "편명: " + airplane.getFlightNo() + "\n" +
          "출발지: " + airplane.getDepartureAirport() + "\n" +
          "도착지: " + airplane.getArrivalAirport() + "\n" +
          "출발일시: " + airplane.getDepartureDateTime() + "\n" +
          "도착일시: " + airplane.getArrivalDateTime() + "\n" +
          "좌석 등급: " + seatGrade + "\n" +
          "결제 금액: " + seat.getPrice() + "원\n\n" +
          "감사합니다.\nCNU Airline 드림";

      message.setText(flightInfo);
      mailSender.send(message);
    } catch (Exception e) {
      System.err.println("메일 전송 실패: " + e.getMessage());
    }

    redirectAttributes.addFlashAttribute("successMessage", "예약이 완료되었습니다!");
    return "redirect:/flights/search";
  }

  @PostMapping("/reserve/cancel")
  public String cancel(@RequestParam Long reserveId, HttpSession session, RedirectAttributes redirectAttributes) {
    Customer customer = (Customer) session.getAttribute("customer");
    if (customer == null) return "redirect:/login";

    Reserve reserve = reserveRepository.findById(reserveId)
        .orElseThrow(() -> new IllegalArgumentException("예약 정보 없음"));

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime departure = reserve.getAirplane().getDepartureDateTime();

    long daysBetween = java.time.Duration.between(now.toLocalDate().atStartOfDay(), departure.toLocalDate().atStartOfDay()).toDays();
    int penalty;

    if (daysBetween >= 15) {
      penalty = 150_000;
    } else if (daysBetween >= 4) {
      penalty = 180_000;
    } else if (daysBetween >= 1) {
      penalty = 250_000;
    } else {
      penalty = reserve.getPayment();
    }

    int refund = Math.max(0, reserve.getPayment() - penalty);

    Cancel cancel = new Cancel();
    cancel.setAirplane(reserve.getAirplane());
    cancel.setCustomer(customer);
    cancel.setSeatClass(reserve.getSeatClass());
    cancel.setRefund(refund);
    cancel.setCancelDateTime(now);

    cancelRepository.save(cancel);
    reserveRepository.delete(reserve);

    redirectAttributes.addFlashAttribute("successMessage", "항공권이 취소되었습니다. 환불 금액: " + refund + "원");
    return "redirect:/my/reservations";
  }

  @GetMapping("/my/reservations")
  public String showMyReservations(HttpSession session, Model model) {
    Customer customer = (Customer) session.getAttribute("customer");
    if (customer == null) {
      return "redirect:/customers/login";
    }

    model.addAttribute("reserves", reserveRepository.findByCustomerCno(customer.getCno()));
    return "my-reservations"; // ✅ templates/my-reservations.html
  }

}
