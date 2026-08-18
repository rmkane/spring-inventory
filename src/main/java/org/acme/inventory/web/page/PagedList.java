package org.acme.inventory.web.page;

import java.util.List;

import org.springframework.ui.Model;

import org.acme.inventory.dto.page.PageResult;
import org.acme.inventory.dto.page.SortOption;

final class PagedList {

    static final List<SortOption> PRODUCTS = List.of(
            new SortOption("name", "Name"),
            new SortOption("price", "Price"),
            new SortOption("quantityOnHand", "On hand"),
            new SortOption("quantityReserved", "Reserved"),
            new SortOption("createdAt", "Created"),
            new SortOption("updatedAt", "Updated"));

    static final List<SortOption> CUSTOMERS = List.of(
            new SortOption("name", "Name"),
            new SortOption("email", "Email"),
            new SortOption("createdAt", "Created"),
            new SortOption("updatedAt", "Updated"));

    static final List<SortOption> INVENTORY = List.of(
            new SortOption("productId", "Product"),
            new SortOption("quantityOnHand", "On hand"),
            new SortOption("quantityReserved", "Reserved"),
            new SortOption("updatedAt", "Updated"));

    static final List<SortOption> CARTS = List.of(
            new SortOption("createdAt", "Created"),
            new SortOption("updatedAt", "Updated"));

    static final List<SortOption> ORDERS = List.of(
            new SortOption("status", "Status"),
            new SortOption("createdAt", "Created"),
            new SortOption("updatedAt", "Updated"),
            new SortOption("paidAt", "Paid"),
            new SortOption("shippedAt", "Shipped"),
            new SortOption("completedAt", "Completed"),
            new SortOption("cancelledAt", "Cancelled"));

    static final List<Integer> PAGE_SIZES = List.of(10, 15, 20, 25, 50);

    private PagedList() {
    }

    static void add(Model model, PageResult<?> page, List<SortOption> sortOptions) {
        model.addAttribute("pageQuery", page.query());
        model.addAttribute("page", page);
        model.addAttribute("sortOptions", sortOptions);
        model.addAttribute("pageSizes", PAGE_SIZES);
    }
}
