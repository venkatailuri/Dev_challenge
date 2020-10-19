package com.db.awmd.challenge.web;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.AmountTransfer;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.InsufficientBalanceException;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.EmailNotificationService;

import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/accounts")
@Slf4j
public class AccountsController {

  private final AccountsService accountsService;
  
  private final EmailNotificationService emailNotificationService;

  @Autowired
  public AccountsController(AccountsService accountsService) {
    this.accountsService = accountsService;
	this.emailNotificationService = new EmailNotificationService();
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> createAccount(@RequestBody @Valid Account account) {
    log.info("Creating account {}", account);

    try {
    this.accountsService.createAccount(account);
    } catch (DuplicateAccountIdException daie) {
      return new ResponseEntity<>(daie.getMessage(), HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @GetMapping(path = "/{accountId}")
  public Account getAccount(@PathVariable String accountId) {
    log.info("Retrieving account for id {}", accountId);
    return this.accountsService.getAccount(accountId);
  }
  
  @PostMapping(path = "/amounttransfer", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> amountTransfer(@RequestBody @Valid AmountTransfer amountTransfer) {
    log.info("Transfering Amount {}", amountTransfer);

    try {
    this.accountsService.amountTransfer(amountTransfer);
    } catch (InsufficientBalanceException daie) {
      return new ResponseEntity<>(daie.getMessage(), HttpStatus.BAD_REQUEST);
    }
    log.info(amountTransfer.getTransferAmount()+" Amount transferred from the Account "+amountTransfer.getFromAccountId()+" to the Account "+amountTransfer.getToAccountId());
    
    emailNotificationService.notifyAboutTransfer(this.accountsService.getAccount(amountTransfer.getFromAccountId()), 
    		"Dear User, Your account debited with the Amount "+amountTransfer.getTransferAmount()+" and transferred to the Account "+amountTransfer.getToAccountId());
    log.info("Dear User, Your account debited with the Amount "+amountTransfer.getTransferAmount()+" and transferred to the Account "+amountTransfer.getToAccountId());
    
    emailNotificationService.notifyAboutTransfer(this.accountsService.getAccount(amountTransfer.getToAccountId()), 
    		"Dear User, Your account credited with the Amount "+amountTransfer.getTransferAmount()+" from the Account "+amountTransfer.getFromAccountId());
    log.info("Dear User, Your account credited with the Amount "+amountTransfer.getTransferAmount()+" from the Account "+amountTransfer.getFromAccountId());
           
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
