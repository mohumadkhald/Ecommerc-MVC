package org.springframework.samples.petclinic.app.cart;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Cart findByUserId(Long userId);

    boolean deleteByUserId(Long userId);

}