package com.ShoeStore.controller.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminSaleManager {

	@GetMapping("/admin/sale")
	public String dashBoard() {
		return "admin/sale";
	}
}
