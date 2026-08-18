package org.acme.inventory.web.page;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Hidden;

import lombok.RequiredArgsConstructor;

import org.acme.inventory.dto.page.PageQuery;
import org.acme.inventory.dto.page.PageResult;
import org.acme.inventory.service.ProductService;

@Hidden
@Controller
@RequestMapping("/ui/products")
@RequiredArgsConstructor
public class ProductPageController {

    private final ProductService productService;

    @GetMapping
    public String list(@ModelAttribute("pageQuery") PageQuery pageQuery, Model model) {
        PageResult<?> page = productService.getProducts(pageQuery);
        PagedList.add(model, page, PagedList.PRODUCTS);
        model.addAttribute("products", page.content());
        return "products/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable UUID id, Model model) {
        model.addAttribute("product", productService.getProductById(id));
        return "products/detail";
    }
}
