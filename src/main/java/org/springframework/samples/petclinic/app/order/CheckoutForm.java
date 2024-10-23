package org.springframework.samples.petclinic.app.order;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CheckoutForm {
    private String cardNumber;
    private String cardHolderName;
    private Date expiryDate;
    private String cvv;
}