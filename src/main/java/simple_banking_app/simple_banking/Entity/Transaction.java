package simple_banking_app.simple_banking.Entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long Id;

  private BigDecimal amount;

  private String type;

  private LocalDateTime timestamp;

  @ManyToOne
  @JoinColumn(name = "account_id")
  private Account account;

  public Transaction() {
  }

  public Transaction(BigDecimal amount, String type, LocalDateTime timestamp, Account account) {
    this.amount = amount;
    this.type = type;
    this.timestamp = timestamp;
    this.account = account;
  }

  public Long getId() {
    return Id;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public String getType() {
    return type;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public Account getAccount() {
    return account;
  }

}
