package org.springframework.samples.petclinic.app.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    List<Category> findByParentIsNull(); // Find top-level categories
    
    List<Category> findByParentId(Long parentId); // Find child categories by parent ID

    List<Category> findAll();

    @Query("SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.children LEFT JOIN FETCH c.products WHERE c.parent IS NULL")
    List<Category> findByParentIsNullFetchChildrenAndProducts();

    @Query("SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.products WHERE c.children IS EMPTY AND c.parent IS NOT NULL")
    List<Category> findLastChildrenWithProducts();
}
