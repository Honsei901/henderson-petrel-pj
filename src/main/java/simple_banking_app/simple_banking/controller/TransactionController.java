package simple_banking_app.simple_banking.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import simple_banking_app.simple_banking.entity.Account;
import simple_banking_app.simple_banking.repository.AccountRepository;

@RestController
@RequestMapping("/api")
public class TransactionController {

  @Autowired
  private AccountRepository accountRepository;

  @PostMapping("/money/withdraw")
  public ResponseEntity<Void> withdrawMone(Authentication authentication) {
    String username = authentication.getName();
    Account account = accountRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("Account not found with username: " + username));
  }
}
