package com.ShoeStore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class CartController {
	@GetMapping("/cart")
	public String cart() {
		return "client/cart";
	}
	

}
