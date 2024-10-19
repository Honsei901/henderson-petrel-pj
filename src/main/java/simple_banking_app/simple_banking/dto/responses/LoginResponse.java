package simple_banking_app.simple_banking.dto.responses;

public class LoginResponse {
  private String token;
  private String message;

  public LoginResponse(String token, String message) {
    this.token = token;
    this.message = message;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

}
