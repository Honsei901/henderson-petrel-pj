package simple_banking_app.simple_banking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import simple_banking_app.simple_banking.Entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
  List<Transaction> findByAccountId(Long accountId);
}
