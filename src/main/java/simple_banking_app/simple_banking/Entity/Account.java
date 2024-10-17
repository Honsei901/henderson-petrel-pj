package simple_banking_app.simple_banking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "accounts")
public class Account {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String username;

  @JsonIgnore
  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private Double deposit;

  public Account() {
  }

  public Account(String username, String password, Double deposit) {
    this.username = username;
    this.password = password;
    this.deposit = deposit;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Double getDeposit() {
    return deposit;
  }

  public void setDeposit(Double deposit) {
    this.deposit = deposit;
  }

}
