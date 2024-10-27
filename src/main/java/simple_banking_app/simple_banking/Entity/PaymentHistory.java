package simple_banking_app.simple_banking.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_history")
public class PaymentHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "sender_account_id", nullable = false)
  private Account senderAccountId;

  @ManyToOne
  @JoinColumn(name = "recipient_account_id", nullable = false)
  private Account recipientAccountId;

  @Column(nullable = false)
  private Double amount;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  public PaymentHistory() {
  }

  public PaymentHistory(Account senderAccountId, Account recipientAccountId, Double amount) {
    this.senderAccountId = senderAccountId;
    this.recipientAccountId = recipientAccountId;
    this.amount = amount;
  }

  @PrePersist
  private void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  private void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

}
