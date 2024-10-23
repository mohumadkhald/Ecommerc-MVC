package org.springframework.samples.petclinic.app.products;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VariationProductRepository extends JpaRepository<ProductVariation, Integer> {


    ProductVariation findByProduct_IdAndSizeAndColor(Long product_id, Size size, Color color);
}
