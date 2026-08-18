package org.acme.inventory.web.page;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Hidden;

import lombok.RequiredArgsConstructor;

import org.acme.inventory.domain.Cart;
import org.acme.inventory.domain.Order;
import org.acme.inventory.service.CartService;
import org.acme.inventory.service.CustomerService;
import org.acme.inventory.service.OrderService;

@Hidden
@Controller
@RequestMapping("/ui/customers")
@RequiredArgsConstructor
public class CustomerPageController {

    private final CustomerService customerService;
    private final CartService cartService;
    private final OrderService orderService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("customers", customerService.getCustomers());
        model.addAttribute("cartsByCustomer", cartsByCustomer());
        model.addAttribute("ordersByCustomer", ordersByCustomer());
        return "customers/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable UUID id, Model model) {
        model.addAttribute("customer", customerService.getCustomerById(id));
        model.addAttribute("carts", cartService.getCartsByCustomerId(id));
        model.addAttribute("orders", orderService.getOrdersByCustomerId(id));
        return "customers/detail";
    }

    private Map<UUID, List<Cart>> cartsByCustomer() {
        return cartService.getCarts().stream().collect(Collectors.groupingBy(Cart::customerId));
    }

    private Map<UUID, List<Order>> ordersByCustomer() {
        return orderService.getOrders().stream().collect(Collectors.groupingBy(Order::customerId));
    }
}
