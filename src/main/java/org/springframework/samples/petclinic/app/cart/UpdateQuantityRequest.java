package org.springframework.samples.petclinic.app.cart;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UpdateQuantityRequest {
    private Long cartItemId;
    private int newQuantity;
}
