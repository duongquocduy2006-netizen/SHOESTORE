package com.ShoeStore.controller.admin;

import com.ShoeStore.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminCustomerManager {
    
    @Autowired
    private CustomerService customerService;

    @GetMapping("/admin/customers")
    public String dashBoard(Model model) {
        // Đẩy danh sách khách hàng sang giao diện Thymeleaf
        model.addAttribute("customers", customerService.getAllCustomers());
        return "admin/customers";
    }
}