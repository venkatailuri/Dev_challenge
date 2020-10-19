package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.AmountTransfer;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.InsufficientBalanceException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

  private final Map<String, Account> accounts = new ConcurrentHashMap<>();

  @Override
  public void createAccount(Account account) throws DuplicateAccountIdException {
    Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
    if (previousAccount != null) {
      throw new DuplicateAccountIdException(
        "Account id " + account.getAccountId() + " already exists!");
    }
  }

  @Override
  public Account getAccount(String accountId) {
    return accounts.get(accountId);
  }

  @Override
  public void clearAccounts() {
    accounts.clear();
  }
  
@Override
  public void amountTransfer(AmountTransfer amountTransfer) throws DuplicateAccountIdException {	  
	Account fromAccount = accounts.get(amountTransfer.getFromAccountId());
	Account toAccount = accounts.get(amountTransfer.getToAccountId());	
	if (fromAccount == null) {
	      throw new InsufficientBalanceException(
	        "The Account " + amountTransfer.getFromAccountId() + " is not exist to Transfer Amount!");
	} else if (toAccount == null) {
	      throw new InsufficientBalanceException(
	    	"The Account " + amountTransfer.getToAccountId() + " is not exist to Transfer Amount!");
	} 
	
	BigDecimal remainBal = (fromAccount.getBalance()).subtract(amountTransfer.getTransferAmount());
	if (remainBal.intValue() < 0) {
      throw new InsufficientBalanceException(
        "In Sufficient Balance in the Account " + amountTransfer.getFromAccountId() + " to Transfer Amount!");
    }
    fromAccount.setBalance(remainBal);
    
    toAccount.setBalance((toAccount.getBalance()).add(amountTransfer.getTransferAmount())); 
    accounts.put(fromAccount.getAccountId(), fromAccount);
    accounts.put(toAccount.getAccountId(), toAccount);
  }

}
