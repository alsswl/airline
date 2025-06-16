package com.airline.Controller;

import com.airline.repository.CustomerRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/customers")
public class CustomerController {

  private final CustomerRepository customerRepository;

  @GetMapping("/login")
  public String loginForm() {
    return "login";
  }

  @PostMapping("/login")
  public String login(@RequestParam String cno,
      @RequestParam String passwd,
      HttpSession session,
      Model model) {
    return customerRepository.findById(cno)
        .filter(c -> c.getPasswd().equals(passwd))
        .map(c -> {
          session.setAttribute("customer", c);
          return "redirect:/dashboard";
        })
        .orElseGet(() -> {
          model.addAttribute("error", "회원 정보가 일치하지 않습니다.");
          return "login";
        });
  }

  @GetMapping("/logout")
  public String logout(HttpSession session) {
    session.invalidate();
    return "redirect:/customers/login";
  }
}

