package simple_banking_app.simple_banking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import simple_banking_app.simple_banking.dto.responses.AccountResponse;
import simple_banking_app.simple_banking.entity.Account;
import simple_banking_app.simple_banking.repository.AccountRepository;

@RestController
@RequestMapping("/api")
public class TestController {

  @Autowired
  private AccountRepository accountRepository;

  @GetMapping("/user")
  public ResponseEntity<AccountResponse> getUserInfo(Authentication authentication) {
    String username = authentication.getName();
    Account account = accountRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("Invalid username or password!"));
    AccountResponse response = new AccountResponse(account.getId(), account.getUsername(), account.getDeposit());
    return ResponseEntity.ok(response);
  }
}
