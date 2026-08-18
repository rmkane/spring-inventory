package org.acme.inventory.web.page;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Hidden;

import lombok.RequiredArgsConstructor;

import org.acme.inventory.service.InventoryService;

@Hidden
@Controller
@RequestMapping("/ui/inventory")
@RequiredArgsConstructor
public class InventoryPageController {

    private final InventoryService inventoryService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("inventory", inventoryService.getInventory());
        return "inventory/list";
    }
}
