package com.ShoeStore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MembershipController {
    
    @GetMapping("/membership")
    public String member() {
    	return "client/membership";
    }
}