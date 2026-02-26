package com.ShoeStore.controller.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;

@Controller
public class AdminPromotionsManager {
	@GetMapping("/admin/promotions")
	public String dashBoard() {
		return "admin/promotions";
	}

}
