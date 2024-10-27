package simple_banking_app.simple_banking.repository;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import simple_banking_app.simple_banking.entity.PaymentHistory;

@Repository
public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {

  List<PaymentHistory> findBySenderAccountId(Long id);

  List<PaymentHistory> findByRecipientAccountId(Long id);
}
