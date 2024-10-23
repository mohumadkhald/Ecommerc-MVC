package org.springframework.samples.petclinic.app.products;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.app.category.Category;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(Category category);
}