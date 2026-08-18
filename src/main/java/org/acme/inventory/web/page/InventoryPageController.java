package org.acme.inventory.web.page;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Hidden;

import lombok.RequiredArgsConstructor;

import org.acme.inventory.domain.Product;
import org.acme.inventory.dto.page.PageQuery;
import org.acme.inventory.dto.page.PageResult;
import org.acme.inventory.service.InventoryService;
import org.acme.inventory.service.ProductService;

@Hidden
@Controller
@RequestMapping("/ui/inventory")
@RequiredArgsConstructor
public class InventoryPageController {

    private final InventoryService inventoryService;
    private final ProductService productService;

    @GetMapping
    public String list(@ModelAttribute("pageQuery") PageQuery pageQuery, Model model) {
        PageResult<?> page = inventoryService.getInventory(pageQuery);
        PagedList.add(model, page, PagedList.INVENTORY);
        model.addAttribute("inventory", page.content());
        model.addAttribute("productsById", productsById());
        return "inventory/list";
    }

    private Map<UUID, Product> productsById() {
        return productService.getProducts().stream()
                .collect(Collectors.toMap(Product::id, product -> product));
    }
}
