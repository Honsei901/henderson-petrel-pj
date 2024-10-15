package simple_banking_app.simple_banking.controller;

import java.util.List;
import java.math.BigDecimal;

import org.hibernate.TransactionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import simple_banking_app.simple_banking.Entity.Account;
import simple_banking_app.simple_banking.Entity.Transaction;
import simple_banking_app.simple_banking.service.AccountService;

@RestController
@RequestMapping("/api")
public class BankController {

  @Autowired
  private AccountService accountService;

  @GetMapping("/account")
  public Account dashboard() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    Account account = accountService.findAccountByUsername(username);
    return account;
  }

  @PostMapping("/register")
  public ResponseEntity<String> registerAccount(@RequestParam String username, @RequestParam String password) {
    try {
      accountService.registerAccount(username, password);
      return ResponseEntity.ok("Account registerd successfully.");
    } catch (RuntimeException e) {
      String errorMessage = e.getMessage();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed: " + errorMessage);
    }
  }

  @PostMapping("/deposit")
  public ResponseEntity<String> deposit(@RequestParam BigDecimal amount) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    Account account = accountService.findAccountByUsername(username);
    accountService.deposit(account, amount);
    return ResponseEntity.ok("Deposited successfully.");
  }

  @PostMapping("/withdraw")
  public ResponseEntity<String> withdraw(@RequestParam BigDecimal amount) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    Account account = accountService.findAccountByUsername(username);

    try {
      accountService.withdraw(account, amount);
      return ResponseEntity.ok("Withdrawed successfully.");
    } catch (RuntimeException e) {
      String errorMessage = e.getMessage();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Withdrawal failed: " + errorMessage);
    }
  }

  @GetMapping("/transactions")
  public List<Transaction> transactionHistory() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    Account account = accountService.findAccountByUsername(username);
    return accountService.getTransactionHistory(account);
  }

  @PostMapping("/transfer")
  public ResponseEntity<String> transferAmount(@RequestParam String toUsername, @RequestParam BigDecimal amount) {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    Account fromAccount = accountService.findAccountByUsername(username);

    try {
      accountService.transferAmount(fromAccount, toUsername, amount);
      return ResponseEntity.ok("Transfer successfully.");
    } catch (TransactionException e) {
      String errorMessage = e.getMessage();
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Transfer failed: " + errorMessage);
    }
  }
}
