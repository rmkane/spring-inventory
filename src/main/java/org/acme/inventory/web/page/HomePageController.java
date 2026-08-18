package org.acme.inventory.web.page;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.v3.oas.annotations.Hidden;

import lombok.RequiredArgsConstructor;

import org.acme.inventory.service.CartService;
import org.acme.inventory.service.CustomerService;
import org.acme.inventory.service.InventoryService;
import org.acme.inventory.service.OrderService;
import org.acme.inventory.service.ProductService;

@Hidden
@Controller
@RequiredArgsConstructor
public class HomePageController {

    private final ProductService productService;
    private final CustomerService customerService;
    private final InventoryService inventoryService;
    private final CartService cartService;
    private final OrderService orderService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("productCount", productService.count());
        model.addAttribute("customerCount", customerService.count());
        model.addAttribute("inventoryCount", inventoryService.count());
        model.addAttribute("cartCount", cartService.count());
        model.addAttribute("orderCount", orderService.count());
        return "index";
    }
}
