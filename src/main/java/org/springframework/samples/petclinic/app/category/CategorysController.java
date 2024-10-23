package org.springframework.samples.petclinic.app.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@RestController
//@RequestMapping("/categories")
//public class CategorysController {
//
//    @Autowired
//    private CategoryService categoryService;
//
//    @GetMapping
//    public List<Category> getAllCategories() {
//        return categoryService.getAllCategories();
//    }
//
//    @GetMapping("/{id}")
//    public Category getCategoryById(@PathVariable Long id) {
//        return categoryService.getCategoryById(id);
//    }
//
//    @GetMapping("/top-level")
//    public List<Category> getTopLevelCategories() {
//        return categoryService.getTopLevelCategories();
//    }
//
//    @GetMapping("/{parentId}/children")
//    public List<Category> getChildCategories(@PathVariable Long parentId) {
//        return categoryService.getChildCategories(parentId);
//    }
//
//    @PostMapping
//    public Category createCategory(@RequestBody Category category) {
//        return categoryService.saveOrUpdate(category);
//    }
//
//    @PutMapping("/{id}")
//    public Category updateCategory(@PathVariable Long id, @RequestBody Category category) {
//        category.setId(id); // Ensure the ID from the path is set
//        return categoryService.saveOrUpdate(category);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
//        categoryService.deleteCategory(id);
//        return ResponseEntity.ok().build();
//    }
//}
