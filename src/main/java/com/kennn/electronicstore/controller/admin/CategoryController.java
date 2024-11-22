package com.kennn.electronicstore.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import java.util.Optional;
import com.kennn.electronicstore.domain.Category;
import com.kennn.electronicstore.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/admin/category")
public class CategoryController {
    private CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping({ "/", "" })
    public String getManageCategoryPage(Model model) {
        List<Category> categories = this.categoryService.findAll();
        model.addAttribute("categories", categories);
        return "admin/page/category/index";
    }

    @GetMapping("/create")
    public String getCreateCategoryPage(Model model) {
        Category newCategory = new Category();

        model.addAttribute("newCategory", newCategory);
        return "admin/page/category/create";
    }

    @PostMapping("/create")
    public String createCategory(@Valid @ModelAttribute("newCategory") Category category, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/page/category/create";
        }
        Category newCategory = new Category();
        newCategory.setName(category.getName());
        newCategory.setStatus(category.isStatus());

        this.categoryService.save(newCategory);

        return "redirect:/admin/category";
    }

    @GetMapping("/update/{id}")
    public String getUpdateCategory(@PathVariable long id, Model model) {
        Optional<Category> optionalCategory = this.categoryService.findById(id);
        if (optionalCategory.isPresent()) {
            Category updateCategory = optionalCategory.get();
            model.addAttribute("updateCategory", updateCategory);
        }
        return "admin/page/category/update";
    }

    @PostMapping("/update")
    public String updateCategory(@ModelAttribute("updateCategory") Category category) {
        Optional<Category> optionalCategory = this.categoryService.findById(category.getId());
        if (optionalCategory.isPresent()) {
            Category updateCategory = optionalCategory.get();
            updateCategory.setName(category.getName());
            updateCategory.setStatus(category.isStatus());

            this.categoryService.save(updateCategory);
        }
        return "redirect:/admin/category";
    }

    @GetMapping("/delete/{id}")
    public String getDeleteCategoryPage(@PathVariable long id, Model model) {
        Optional<Category> optionalCategory = this.categoryService.findById(id);
        if (optionalCategory.isPresent()) {
            Category deleteCategory = optionalCategory.get();
            model.addAttribute("deleteCategory", deleteCategory);
        }
        return "admin/page/category/delete";
    }

    @PostMapping("/delete")
    public String deleteCategory(@ModelAttribute("deleteCategory") Category category) {
        Optional<Category> optionalCategory = this.categoryService.findById(category.getId());
        if (optionalCategory.isPresent()) {
            Category deleteCategory = optionalCategory.get();
            this.categoryService.remove(deleteCategory);
        }

        return "redirect:/admin/category";
    }

    @GetMapping("/change-status/{id}")
    public String changeStatusCategory(@PathVariable long id) {
        Optional<Category> optionalCategory = this.categoryService.findById(id);
        if (optionalCategory.isPresent()) {
            Category category = optionalCategory.get();
            category.setStatus(!category.isStatus());

            this.categoryService.save(category);
        }
        return "redirect:/admin/category";
    }

}
