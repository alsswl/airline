package com.airline.controller;

import com.airline.entity.Customer;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

  @GetMapping("/dashboard")
  public String dashboard(HttpSession session, Model model) {
    Customer customer = (Customer) session.getAttribute("customer");
    if (customer == null) return "redirect:/customers/login";
    model.addAttribute("customer", customer);
    return "dashboard";
  }
}
