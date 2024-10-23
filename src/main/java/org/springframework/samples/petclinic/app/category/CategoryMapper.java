package org.springframework.samples.petclinic.app.category;

import org.springframework.stereotype.Service;

@Service
public class CategoryMapper {
    public Category toCategory(CategoryDto categoryDto)
    {
        var category = new Category();
        category.setName(categoryDto.getName());
        var categoryParent = new Category();
        if (categoryDto.getParentId() != null){
            categoryParent.setId(categoryDto.getParentId());
        }
        categoryParent.setId(1L);
        category.setParent(categoryParent);
        return category;
    }

    public CategoryDto toCategoryDto(Category category)
    {
        return new CategoryDto(category.getName(), category.getParent().getId());
    }
}
