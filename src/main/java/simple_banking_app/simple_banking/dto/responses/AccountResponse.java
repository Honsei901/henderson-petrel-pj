package simple_banking_app.simple_banking.dto.responses;

public class AccountResponse {
  private Long id;
  private String username;
  private Double deposit;

  public AccountResponse(Long id, String username, Double deposit) {
    this.id = id;
    this.username = username;
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

  public Double getDeposit() {
    return deposit;
  }

  public void setDeposit(Double deposit) {
    this.deposit = deposit;
  }

}
