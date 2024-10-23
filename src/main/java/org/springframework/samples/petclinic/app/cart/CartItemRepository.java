package org.springframework.samples.petclinic.app.cart;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCartId(Long cartId);

    CartItem findByProductVariationIdAndCartId(Long id, Long cartId);

    CartItem findByProductVariationId(Long id);
}