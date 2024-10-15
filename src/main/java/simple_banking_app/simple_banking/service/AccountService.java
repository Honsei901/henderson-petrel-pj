package simple_banking_app.simple_banking.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import simple_banking_app.simple_banking.Entity.Account;
import simple_banking_app.simple_banking.Entity.Transaction;
import simple_banking_app.simple_banking.repository.AccountRepository;
import simple_banking_app.simple_banking.repository.TransactionRepository;

@Service
public class AccountService implements UserDetailsService {

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private TransactionRepository transactionRepository;

  /**
   * Find an account by username.
   * 
   * @param username
   * @return
   */
  public Account findAccountByUsername(String username) {
    return accountRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Account not found."));
  }

  /**
   * Create new account.
   * 
   * @param username
   * @param password
   * @return
   */
  public Account registerAccount(String username, String password) throws RuntimeException {
    if (accountRepository.findByUsername(username).isPresent()) {
      new RuntimeException("Account already exists.");
    }

    Account account = new Account();
    account.setUsername(username);
    account.setPassword(passwordEncoder.encode(password));
    account.setBalance(BigDecimal.ZERO);
    return accountRepository.save(account);

  }

  /**
   * Deposit money.
   * 
   * @param account
   * @param amount
   */
  public void deposit(Account account, BigDecimal amount) {
    account.setBalance(account.getBalance().add(amount));
    accountRepository.save(account);

    Transaction transaction = new Transaction(
        amount,
        "Deposit",
        LocalDateTime.now(),
        account);

    transactionRepository.save(transaction);
  }

  /**
   * Withdraw money.
   * 
   * @param account
   * @param amount
   * @throws RuntimeException
   */
  public void withdraw(Account account, BigDecimal amount) throws RuntimeException {
    if (account.getBalance().compareTo(amount) < 0) {
      throw new RuntimeException("Insufficient funds.");
    }
    account.setBalance(account.getBalance().subtract(amount));
    accountRepository.save(account);

    Transaction transaction = new Transaction(amount, "Withdrawal", LocalDateTime.now(), account);
    transactionRepository.save(transaction);
  }

  /**
   * Get account's transaction history.
   * 
   * @param account
   * @return
   */
  public List<Transaction> getTransactionHistory(Account account) {
    return transactionRepository.findByAccountId(account.getId());
  }

  /**
   * Login.
   * 
   * @param username
   * @return
   */
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Account account = findAccountByUsername(username);
    if (account == null) {
      throw new UsernameNotFoundException("Username or Password not found.");
    }

    return new Account(
        account.getUsername(),
        account.getPassword(),
        account.getBalance(),
        account.getTransactions(),
        authorities());
  }

  /**
   * To transfer money between users.
   * 
   * @param fromAccount
   * @param toUsername
   * @param amount
   * @throws RuntimeException
   */
  @Transactional
  public void transferAmount(Account fromAccount, String toUsername, BigDecimal amount) throws RuntimeException {
    if (fromAccount.getBalance().compareTo(amount) < 0) {
      throw new RuntimeException("Insufficient funds.");
    }

    Account toAccount = accountRepository.findByUsername(toUsername)
        .orElseThrow(() -> new RuntimeException("Recipient account not found."));

    // Deduct
    fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
    accountRepository.save(fromAccount);

    // Add
    toAccount.setBalance(toAccount.getBalance().add(amount));
    accountRepository.save(toAccount);

    // Create transaction records
    Transaction debitTransaction = new Transaction(
        amount,
        "Transfer Out to " + toAccount.getUsername(),
        LocalDateTime.now(),
        fromAccount);
    transactionRepository.save(debitTransaction);

    Transaction creditTransaction = new Transaction(
        amount,
        "Transfer In to " + fromAccount.getUsername(),
        LocalDateTime.now(),
        toAccount);
    transactionRepository.save(creditTransaction);
  }

  /**
   * 
   * @return
   */
  private Collection<? extends GrantedAuthority> authorities() {
    return Arrays.asList(new SimpleGrantedAuthority("User"));
  }
}
