package org.springframework.samples.petclinic.app.products;

import lombok.AllArgsConstructor;
import org.springframework.samples.petclinic.app.category.Category;
import org.springframework.samples.petclinic.app.category.CategoryService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@AllArgsConstructor
public class ProductController {
    private ProductService productService;
    private CategoryService categoryService;

    @GetMapping("/products/allproducts")
    public String index(Model model, Authentication authentication) {

        Product product = new Product();
        model.addAttribute("product", product);
        List<Product> products = productService.findAll();
        List<Category> topCategories = categoryService.findTopLevelCategoriesWithProducts();
        model.addAttribute("topCategories", topCategories);
        model.addAttribute("products", products);

        return "products/index";
    }

    @GetMapping("/products/{id}")
    public String showProductDetails(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);
        if (product == null) {
            return "redirect:/products/allproducts"; // Or an error page
        }
        model.addAttribute("product", product);
        return "products/details";
    }

    @GetMapping("/products/category/{id}")
    public String showCategoryProducts(@PathVariable Long id, Model model) {
        Product product = new Product();
        model.addAttribute("product", product);
        Category category = categoryService.findById(id);
        if (category == null) {
            return "redirect:/products/allproducts"; // Or an error page
        }
        List<Category> topCategories = categoryService.findTopLevelCategoriesWithProducts();
        model.addAttribute("topCategories", topCategories);
        List<Product> products = productService.findByCategory(category);
        model.addAttribute("products", products);
        model.addAttribute("id", id); // Make sure 'id' is added to the model
        return "products/categoryProducts"; // New template to display products of the category
    }


    @GetMapping("/products/addproduct")
    public String add(Model model) {
        model.addAttribute("newProduct", new Product());
        model.addAttribute("lastChildrenCategories", categoryService.findLastChildrenWithProducts());        return "products/add";
    }

    @PostMapping("/products/addProduct")
    public String addProduct(@ModelAttribute Product newProduct) {
        // Save the product with its variations
        productService.save(newProduct);
        return "redirect:/products/allproducts";
    }


}