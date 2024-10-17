package simple_banking_app.simple_banking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import simple_banking_app.simple_banking.entity.Account;
import simple_banking_app.simple_banking.request.SignupRequest;
import simple_banking_app.simple_banking.service.AccountService;

@RestController
@RequestMapping("/api")
public class AuthController {

  @Autowired
  private AccountService accountService;

  @PostMapping("/signup")
  public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest) {
    try {
      Account account = accountService.registerAccount(
          signupRequest.getUsername(),
          signupRequest.getPassword(),
          signupRequest.getDeposit());
      return ResponseEntity.ok(account);
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

}
