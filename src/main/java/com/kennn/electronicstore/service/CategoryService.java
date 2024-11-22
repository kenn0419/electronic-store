package com.kennn.electronicstore.service;

import com.kennn.electronicstore.domain.Category;
import com.kennn.electronicstore.repository.CategoryRepository;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class CategoryService {
    private CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<Category> findAll() {
        return this.categoryRepository.findAll();
    }

    public List<Category> findPublishCategory() {
        return this.categoryRepository.findByStatusTrue();
    }

    public Category save(Category category) {
        return this.categoryRepository.save(category);
    }

    public Optional<Category> findById(long id) {
        return this.categoryRepository.findById(id);
    }

    public void remove(Category category) {
        this.categoryRepository.delete(category);
    }
}
