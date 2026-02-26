package com.ShoeStore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller

public class ProfileController {
	@GetMapping("/profile")
	public String viewProfile() {
		return "client/profile";
	}

	@GetMapping("/orders")
	public String viewOrders() {
		return "client/orders";
	}

	@GetMapping("/change-password")
	public String viewChangePassword() {
		return "client/change-password";
	}
}