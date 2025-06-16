package com.airline.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Customer {
  @Id
  private String cno; // 회원번호 (c0이면 관리자)

  private String name;
  private String passwd;
  private String email;
  private String passportNumber;

  @OneToMany(mappedBy = "customer")
  private List<Reserve> reserves;

  @OneToMany(mappedBy = "customer")
  private List<Cancel> cancels;
}
