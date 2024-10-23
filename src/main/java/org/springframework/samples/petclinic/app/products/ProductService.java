package org.springframework.samples.petclinic.app.products;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.samples.petclinic.app.category.Category;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private VariationProductRepository productVariationRepository;

    public List<Product> findAll() {
        return productRepository.findAll();
    }


    @Transactional
    public Product save(Product product) {
        // Save the product first to generate the ID
        Product savedProduct = productRepository.save(product);

        // Now associate each variation with the saved product
        if (product.getVariations() != null) {
            for (ProductVariation variation : product.getVariations()) {
                variation.setProduct(savedProduct);  // Set the product reference
                productVariationRepository.save(variation);
            }
        }

        return savedProduct;
    }

    public List<Product> findByCategory(Category category) {
        return productRepository.findByCategory(category);
    }

    public void addProductVariation(Long productId, ProductVariation variation) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));

        variation.setProduct(product);
        product.getVariations().add(variation);

        productRepository.save(product);
    }

    public Product findById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

}