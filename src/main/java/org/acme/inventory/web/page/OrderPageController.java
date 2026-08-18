package org.acme.inventory.web.page;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import io.swagger.v3.oas.annotations.Hidden;

import lombok.RequiredArgsConstructor;

import org.acme.inventory.domain.Customer;
import org.acme.inventory.dto.page.PageQuery;
import org.acme.inventory.dto.page.PageResult;
import org.acme.inventory.service.CustomerService;
import org.acme.inventory.service.OrderService;

@Hidden
@Controller
@RequestMapping("/ui/orders")
@RequiredArgsConstructor
public class OrderPageController {

    private final OrderService orderService;
    private final CustomerService customerService;

    @GetMapping
    public String list(@ModelAttribute("pageQuery") PageQuery pageQuery, Model model) {
        PageResult<?> page = orderService.getOrders(pageQuery);
        PagedList.add(model, page, PagedList.ORDERS);
        model.addAttribute("orders", page.content());
        model.addAttribute("customersById", customersById());
        return "orders/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable UUID id, Model model) {
        var order = orderService.getOrderById(id);
        model.addAttribute("order", order);
        model.addAttribute("customer", customerService.getCustomerById(order.getCustomerId()));
        return "orders/detail";
    }

    private Map<UUID, Customer> customersById() {
        return customerService.getCustomers().stream()
                .collect(Collectors.toMap(Customer::getId, Function.identity()));
    }
}
