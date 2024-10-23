package org.springframework.samples.petclinic.app.category;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

//    public List<Category> getAllCategories() {
//        return categoryRepository.findAll();
//    }
//
//    public Category getCategoryById(Long id) {
//        return categoryRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Category with id " + id + " not found"));
//    }
//
//    public List<Category> getTopLevelCategories() {
//        return categoryRepository.findByParentIsNull();
//    }
//
//    public List<Category> getChildCategories(Long parentId) {
//        return categoryRepository.findByParentId(parentId);
//    }
//
//    public Category saveOrUpdate(Category category) {
//        return categoryRepository.save(category);
//    }
//
//    public void deleteCategory(Long id) {
//        categoryRepository.deleteById(id);
//    }



    @Transactional
    public void save(Category category) {
        if (category.getParent() != null && category.getParent().getId() == null) {
            category.setParent(null);
        } else if (category.getParent() != null) {
            Category parentCategory = categoryRepository.findById(category.getParent().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent category not found"));
            category.setParent(parentCategory);
        }
        categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Category> getTopLevelCategories() {
        return categoryRepository.findByParentIsNull();
    }

    public boolean deleteCategoryById(Long categoryId) {
        categoryRepository.deleteById(categoryId);
        return true;
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }
    public List<Category> findTopLevelCategories() {
        return categoryRepository.findByParentIsNull();
    }

    public List<Category> findTopLevelCategoriesWithProducts() {
        List<Category> categories = categoryRepository.findByParentIsNullFetchChildrenAndProducts();
        categories.forEach(category -> {
            System.out.println("Category: " + category.getName() + ", Children: " + category.getChildren().size());
        });
        return categories;
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
    }

    public Object findLastChildrenWithProducts() {
        return categoryRepository.findLastChildrenWithProducts();
    }

}
