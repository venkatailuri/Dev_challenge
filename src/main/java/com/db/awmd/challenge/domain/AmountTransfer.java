package com.db.awmd.challenge.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

import java.math.BigDecimal;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class AmountTransfer {

  @NotNull
  @NotEmpty
  private String fromAccountId;
  
  @NotNull
  @NotEmpty
  private String toAccountId;

  @NotNull
  @Min(value = 0, message = "Amount must be positive to Transfer.")
  private BigDecimal transferAmount;
  
  public AmountTransfer(String fromAccountId, String toAccountId) {
	    this.fromAccountId = fromAccountId;
	    this.toAccountId = toAccountId;
	    this.transferAmount = BigDecimal.ZERO;
	  }

  @JsonCreator
  public AmountTransfer(@JsonProperty("fromAccountId") String fromAccountId,
		  @JsonProperty("toAccountId") String toAccountId,
    @JsonProperty("transferAmount") BigDecimal transferAmount) {
    this.fromAccountId = fromAccountId;
    this.toAccountId = toAccountId;
    this.transferAmount = transferAmount;
  }
}
