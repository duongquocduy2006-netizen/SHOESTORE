package com.ShoeStore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

	@Autowired
	private JdbcTemplate jdbc;

	@GetMapping("/")
	public String home(Model model) {
		// Lấy 8 sản phẩm mới nhất, kèm tên danh mục, ảnh primary và giá thấp nhất
		String sql = "SELECT TOP 8 p.id, p.product_name, c.category_name as brand_name, " +
				"(SELECT TOP 1 '/images/' + image_url FROM product_images WHERE product_id = p.id ORDER BY is_primary DESC, id ASC) as image_url, "
				+
				"(SELECT MIN(price) FROM product_variants WHERE product_id = p.id) as min_price " +
				"FROM products p " +
				"LEFT JOIN categories c ON p.category_id = c.id " +
				"WHERE p.status = 1 AND c.status = 1 " +
				"AND EXISTS (SELECT 1 FROM brands b WHERE b.brand_name = p.brand_name AND b.status = 1) " +
				"AND EXISTS (SELECT 1 FROM product_variants pv WHERE pv.product_id = p.id AND pv.quantity > 0) " +
				"ORDER BY p.created_at DESC";

		List<Map<String, Object>> latestProducts = jdbc.queryForList(sql);
		model.addAttribute("latestProducts", latestProducts);

		return "client/home";
	}
}