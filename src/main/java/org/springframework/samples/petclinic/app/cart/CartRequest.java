package org.springframework.samples.petclinic.app.cart;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CartRequest {
    private Long productId;
    private String size;
    private String color;
    private int quantity;
}
