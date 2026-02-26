
package com.ShoeStore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProductController {

    @GetMapping("/shop")
    public String viewShop() {
        return "client/shop";
    }

    @GetMapping("/details")
    public String viewProductDetails() {
        return "client/details";
    }
    
    @GetMapping("/flash-sale")
    public String viewFlashSale() {
        return "client/flash-sale";
    }
    
    @GetMapping("/new-arrivals")
    public String viewNewArrivals() {
        return "client/new-arrivals";
    }
}