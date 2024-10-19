package simple_banking_app.simple_banking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import simple_banking_app.simple_banking.dto.requests.LoginRequest;
import simple_banking_app.simple_banking.dto.requests.SignupRequest;
import simple_banking_app.simple_banking.entity.Account;
import simple_banking_app.simple_banking.service.AuthService;

@RestController
@RequestMapping("/api")
public class AuthController {

  @Autowired
  private AuthService authService;

  /**
   * Log the user in.
   * 
   * @param loginRequest
   * @return
   */
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
    try {
      Cookie[] cookies = authService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
      response.addCookie(cookies[0]);
      response.addCookie(cookies[1]);

      return ResponseEntity.ok("Login successful");
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Create new account.
   * 
   * @param signupRequest
   * @return
   */
  @PostMapping("/signup")
  public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest) {
    try {
      Account account = authService.registerAccount(
          signupRequest.getUsername(),
          signupRequest.getPassword(),
          signupRequest.getDeposit());

      return ResponseEntity.ok(account);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  /**
   * Refresh the access token by the refresh token.
   * 
   * @param request
   * @param response
   * @return
   */
  @GetMapping("/refresh-token")
  public ResponseEntity<?> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
    try {
      Cookie accessTokenCookie = authService.generateAccessToken(request);
      response.addCookie(accessTokenCookie);

      return ResponseEntity.ok("Access token refreshed successfully");
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

}
