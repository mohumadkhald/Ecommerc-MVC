package org.springframework.samples.petclinic.app.products;


import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProductVariationService {

    @Autowired
    private final VariationProductRepository variationProductRepository;

    public ProductVariation findById(Integer productVariationId) {
        return variationProductRepository.findById(productVariationId).get();
    }

    public ProductVariation findBySizeAndColor(Long productId, Size size, String color) {
        return variationProductRepository.findByProduct_IdAndSizeAndColor(productId, size, Color.valueOf(color));
    }
}