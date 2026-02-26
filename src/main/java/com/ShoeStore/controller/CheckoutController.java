package com.ShoeStore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CheckoutController {
    
    
    @GetMapping("/checkout")
    public String register() {
        return "client/checkout"; 
    }
}