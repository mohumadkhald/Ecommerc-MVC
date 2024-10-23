package org.springframework.samples.petclinic.app.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/category")
    public String showCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("topLevelCategories", categoryService.getTopLevelCategories());
        return "category";
    }

    @PostMapping("/category")
    public String addCategory(@ModelAttribute("category") Category category) {
        categoryService.save(category);
        return "redirect:/category";
    }

    @PostMapping("/deleteCategory/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        // Assuming CategoryService handles business logic
        boolean deleted = categoryService.deleteCategoryById(categoryId);
        if (deleted) {
            return ResponseEntity.ok().build(); // Category deleted successfully
        } else {
            return ResponseEntity.notFound().build(); // Category not found or delete failed
        }
    }
}
