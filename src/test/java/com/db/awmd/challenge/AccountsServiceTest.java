package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.AmountTransfer;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.service.AccountsService;
import java.math.BigDecimal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountsServiceTest {

  @Autowired
  private AccountsService accountsService;

  @Test
  public void addAccount() throws Exception {
    Account account = new Account("Id-123");
    account.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(account);

    assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
  }

  @Test
  public void addAccount_failsOnDuplicateId() throws Exception {
    String uniqueId = "Id-" + System.currentTimeMillis();
    Account account = new Account(uniqueId);
    this.accountsService.createAccount(account);

    try {
      this.accountsService.createAccount(account);
      fail("Should have failed when adding duplicate account");
    } catch (DuplicateAccountIdException ex) {
      assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
    }

  }
  
  @Test
  public void amountTransfer() throws Exception {
	 
	Account fromAccount = new Account("Id-124");
	fromAccount.setBalance(new BigDecimal(1000));
	this.accountsService.createAccount(fromAccount); 
	
	Account toAccount = new Account("Id-125");
	toAccount.setBalance(new BigDecimal(500));
	this.accountsService.createAccount(toAccount);  
	  
	AmountTransfer amountTransfer = new AmountTransfer("Id-124", "Id-125");
	amountTransfer.setTransferAmount(new BigDecimal(300));
    this.accountsService.amountTransfer(amountTransfer);
    
    assertThat(this.accountsService.getAccount("Id-124")).isEqualTo(fromAccount);
    assertThat(this.accountsService.getAccount("Id-125")).isEqualTo(toAccount);
  }
}
