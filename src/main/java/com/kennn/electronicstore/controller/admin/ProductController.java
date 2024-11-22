package com.kennn.electronicstore.controller.admin;

import java.util.List;
import java.util.Optional;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import com.kennn.electronicstore.domain.Category;
import com.kennn.electronicstore.domain.Product;
import com.kennn.electronicstore.domain.dto.ProductDTO;
import com.kennn.electronicstore.service.CategoryService;
import com.kennn.electronicstore.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/admin/product")
public class ProductController {

    private CategoryService categoryService;
    private ProductService productService;

    public ProductController(CategoryService categoryService, ProductService productService) {
        this.categoryService = categoryService;
        this.productService = productService;
    }

    @GetMapping({ "", "/" })
    public String getManageProductPage(Model model) {
        List<Product> products = this.productService.findAll();

        model.addAttribute("products", products);

        return "admin/page/product/index";
    }

    @GetMapping("/create")
    public String getCreateProductPage(Model model) {
        ProductDTO newProduct = new ProductDTO();
        List<Category> categories = this.categoryService.findPublishCategory();

        model.addAttribute("categories", categories);
        model.addAttribute("newProduct", newProduct);
        return "admin/page/product/create";
    }

    @PostMapping("/create")
    public String createProduct(@Valid @ModelAttribute("newProduct") ProductDTO productDTO, BindingResult result,
            Model model) {
        if (productDTO.getThumbnail().isEmpty()) {
            result.addError(new FieldError("productDTO", "thumbnail", "Thumbnail không được để trống"));
        }

        if (result.hasErrors()) {
            List<Category> categories = this.categoryService.findPublishCategory();
            model.addAttribute("categories", categories);
            return "admin/page/product/create";
        }

        MultipartFile thumbnail = productDTO.getThumbnail();
        Date createdAt = new Date();
        String storageFileName = createdAt.getTime() + "_" + thumbnail.getOriginalFilename();

        try {
            String uploadDir = "public/images/product/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            try (InputStream inputStream = thumbnail.getInputStream()) {
                Files.copy(inputStream, Paths.get(uploadDir + storageFileName), StandardCopyOption.REPLACE_EXISTING);
            }

        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
        Optional<Category> optionalCategory = this.categoryService.findById(productDTO.getCategoryId());
        if (optionalCategory.isPresent()) {
            Category category = optionalCategory.get();

            Product newProduct = new Product();

            newProduct.setTitle(productDTO.getTitle());
            newProduct.setPrice(productDTO.getPrice());
            newProduct.setDescription(productDTO.getDescription());
            newProduct.setSold(productDTO.getSold());
            newProduct.setStock(productDTO.getStock());
            newProduct.setManufactor(productDTO.getManufactor());
            newProduct.setCategory(category);
            newProduct.setThumbnail(storageFileName);
            newProduct.setStatus(productDTO.isStatus());
            newProduct.setPublishedAt(new Date());

            this.productService.save(newProduct);
        }
        return "redirect:/admin/product";
    }

    @GetMapping("/update/{id}")
    public String getUpdateProductPage(@PathVariable long id, Model model) {
        Optional<Product> optionalProduct = this.productService.findById(id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            ProductDTO updateProduct = new ProductDTO();

            updateProduct.setTitle(product.getTitle());
            updateProduct.setPrice(product.getPrice());
            updateProduct.setDescription(product.getDescription());
            updateProduct.setSold(0);
            updateProduct.setStock(product.getStock());
            updateProduct.setManufactor(product.getManufactor());
            updateProduct.setCategoryId(product.getCategory().getId());
            updateProduct.setStatus(product.isStatus());

            List<Category> categories = this.categoryService.findPublishCategory();

            model.addAttribute("categories", categories);
            model.addAttribute("updateProduct", updateProduct);
            model.addAttribute("product", product);
        }
        return "admin/page/product/update";
    }

    @PostMapping("/update")
    public String updateProduct(@RequestParam("id") long id,
            @Valid @ModelAttribute("updateProduct") ProductDTO productDTO, BindingResult result, Model model) {
        Optional<Product> optionalProduct = this.productService.findById(id);
        try {
            if (optionalProduct.isPresent()) {
                Product updateProduct = optionalProduct.get();
                if (result.hasErrors()) {
                    List<Category> categories = this.categoryService.findPublishCategory();
                    model.addAttribute("categories", categories);
                    return "admin/page/product/update";
                }
                if (!productDTO.getThumbnail().isEmpty()) {
                    String uploadDir = "public/images/product/";
                    Path oldPath = Paths.get(uploadDir + updateProduct.getThumbnail());
                    try {
                        Files.delete(oldPath);
                    } catch (Exception e) {
                        System.out.println("Exception: " + e.getMessage());
                    }
                    MultipartFile newThumbnail = productDTO.getThumbnail();
                    Date createdAt = new Date();
                    String storageFileName = createdAt.getTime() + "_" + newThumbnail.getOriginalFilename();

                    try (InputStream inputStream = newThumbnail.getInputStream()) {
                        Files.copy(inputStream, Paths.get(uploadDir + storageFileName),
                                StandardCopyOption.REPLACE_EXISTING);
                    }
                    Optional<Category> optionalCategory = this.categoryService.findById(productDTO.getCategoryId());
                    if (optionalCategory.isPresent()) {
                        updateProduct.setTitle(productDTO.getTitle());
                        updateProduct.setCategory(optionalCategory.get());
                        updateProduct.setDescription(productDTO.getDescription());
                        updateProduct.setManufactor(productDTO.getManufactor());
                        updateProduct.setPrice(productDTO.getPrice());
                        updateProduct.setStatus(productDTO.isStatus());
                        updateProduct.setThumbnail(storageFileName);
                        updateProduct.setStock(updateProduct.getStock());

                        this.productService.save(updateProduct);
                    }

                }
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
        return "redirect:/admin/product";
    }

    @GetMapping("/delete/{id}")
    public String getDeleteProductPage(@PathVariable long id, Model model) {
        Optional<Product> optionalProduct = this.productService.findById(id);
        if (optionalProduct.isPresent()) {
            Product deleteProduct = optionalProduct.get();
            model.addAttribute("deleteProduct", deleteProduct);
        }

        return "admin/page/product/delete";
    }

    @PostMapping("/delete")
    public String deleteProduct(@ModelAttribute("deleteProduct") Product product) {
        Optional<Product> optionalProduct = this.productService.findById(product.getId());
        if (optionalProduct.isPresent()) {
            Product deleteProduct = optionalProduct.get();
            String uploadDir = "public/images/product/";
            Path oldPath = Paths.get(uploadDir + deleteProduct.getThumbnail());
            try {
                Files.delete(oldPath);
            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
            }
            this.productService.remove(deleteProduct);
        }

        return "redirect:/admin/product";
    }

    @GetMapping("/change-status/{id}")
    public String changeStatusProduct(@PathVariable long id) {
        Optional<Product> optionalProduct = this.productService.findById(id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setStatus(!product.isStatus());

            this.productService.save(product);
        }
        return "redirect:/admin/product";
    }

}
