package com.airline.controller;

import com.airline.entity.Customer;
import com.airline.repository.ReserveRepository;
import com.airline.repository.CancelRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AdminStatsController {

  private final ReserveRepository reserveRepository;
  private final CancelRepository cancelRepository;

  @GetMapping("/stats")
  public String showStats(HttpSession session, Model model) {
    Customer customer = (Customer) session.getAttribute("customer");
    if (customer == null || !"c0".equals(customer.getCno())) {
      return "redirect:/login";
    }

    model.addAttribute("flightReservationStats", reserveRepository.countByAirplane());
    model.addAttribute("flightCancelStats", cancelRepository.countByAirplane());
    model.addAttribute("seatClassStats", reserveRepository.sumBySeatClass());
    model.addAttribute("monthlyRevenueStats", reserveRepository.monthlyRevenue());
    model.addAttribute("totalPayment", reserveRepository.totalPayment());
    model.addAttribute("totalRefund", cancelRepository.totalRefund());
    model.addAttribute("customerStats", reserveRepository.customerReserveCount());
    model.addAttribute("cumulativeStats", reserveRepository.findCumulativeReservations());


    return "stats";
  }
}
