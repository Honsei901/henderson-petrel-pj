package simple_banking_app.simple_banking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import simple_banking_app.simple_banking.repository.AccountRepository;
import simple_banking_app.simple_banking.repository.PaymentHistoryRepository;

@Service
public class TransactionService {

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private PaymentHistoryRepository paymentHistoryRepository;
}
