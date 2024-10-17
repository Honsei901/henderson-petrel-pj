package simple_banking_app.simple_banking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import simple_banking_app.simple_banking.entity.Account;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
  boolean existsByUsername(String username);

  Account findByUsername(String username);
}
