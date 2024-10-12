package simple_banking_app.simple_banking.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import simple_banking_app.simple_banking.Entity.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
  Optional<Account> findByUsername(String username);
}
