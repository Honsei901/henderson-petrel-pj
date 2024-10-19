package simple_banking_app.simple_banking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import simple_banking_app.simple_banking.entity.Account;
import simple_banking_app.simple_banking.repository.AccountRepository;

@Service
public class AccountService {
  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  /**
   * ログインのためパスワードが正しいか確認
   * 
   * @param username
   * @param password
   * @return
   * @throws Exception
   */
  public Account authenticate(String username, String password) throws Exception {
    Account account = accountRepository.findByUsername(username)
        .orElseThrow(() -> new Exception("Invalid username or password"));
    if (passwordEncoder.matches(password, account.getPassword())) {
      return account;
    } else {
      throw new Exception("Invalid username or password");
    }
  }

  /**
   * 新規登録
   * 
   * @param username
   * @param password
   * @param deposit
   * @return
   * @throws Exception
   */
  public Account registerAccount(String username, String password, Double deposit) throws Exception {
    if (accountRepository.existsByUsername(username)) {
      throw new Exception("Username already exists");
    }

    String encodedPassword = passwordEncoder.encode(password);

    Account account = new Account(username, encodedPassword, deposit);
    return accountRepository.save(account);
  }
}
