package com.kennn.electronicstore.controller.client;

import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import com.kennn.electronicstore.domain.Product;
import com.kennn.electronicstore.service.ProductService;

import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomepageController {
    private ProductService productService;

    public HomepageController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/")
    public String getHomePage(Model model) {
        List<Product> products = this.productService.findAll();
        model.addAttribute("products", products);
        return "client/page/homepage/index";
    }

}
