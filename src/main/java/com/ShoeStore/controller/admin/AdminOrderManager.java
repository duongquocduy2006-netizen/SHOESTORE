package com.ShoeStore.controller.admin;

import com.ShoeStore.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminOrderManager {
    
    @Autowired
    private OrderService orderService;

    @GetMapping("/admin/orders")
    public String dashBoard(Model model) {
        // Ném toàn bộ danh sách đơn hàng sang View (Thymeleaf)
        model.addAttribute("orders", orderService.getAllOrders());
        return "admin/orders";
    }
}