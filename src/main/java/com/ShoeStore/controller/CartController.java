package com.ShoeStore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CartController {

	@Autowired
	private JdbcTemplate jdbc;

	@GetMapping("/cart")
	public String cart(HttpSession session, Model model) {
		@SuppressWarnings("unchecked")
		Map<String, Object> account = (Map<String, Object>) session.getAttribute("account");
		if (account == null) {
			return "redirect:/login";
		}

		Long accountId = ((Number) account.get("id")).longValue();

		String sql = "SELECT ci.id, ci.quantity, ci.product_variant_id as variant_id, p.id as product_id, " +
				"p.product_name, s.size_name, col.color_name, v.price, v.quantity as stock, " +
				"(SELECT TOP 1 '/images/' + image_url FROM product_images WHERE product_id = p.id ORDER BY is_primary DESC, id ASC) as image_url "
				+
				"FROM cart_items ci " +
				"JOIN product_variants v ON ci.product_variant_id = v.id " +
				"JOIN products p ON v.product_id = p.id " +
				"JOIN sizes s ON v.size_id = s.id " +
				"JOIN colors col ON v.color_id = col.id " +
				"WHERE ci.user_id = ?";

		List<Map<String, Object>> cartItems = jdbc.queryForList(sql, accountId);

		double total = cartItems.stream()
				.mapToDouble(
						item -> ((Number) item.get("price")).doubleValue() * ((Number) item.get("quantity")).intValue())
				.sum();

		model.addAttribute("cartItems", cartItems);
		model.addAttribute("totalPrice", total);

		return "client/cart";
	}

	@PostMapping("/cart/add")
	@ResponseBody
	public Map<String, Object> addToCart(@RequestParam("variantId") Integer variantId,
			@RequestParam("quantity") Integer quantity,
			HttpSession session) {
		Map<String, Object> response = new HashMap<>();
		@SuppressWarnings("unchecked")
		Map<String, Object> account = (Map<String, Object>) session.getAttribute("account");

		if (account == null) {
			response.put("success", false);
			response.put("message", "Chưa đăng nhập");
			return response;
		}

		Long accountId = ((Number) account.get("id")).longValue();

		// 1. Kiểm tra tồn kho
		String stockSql = "SELECT quantity FROM product_variants WHERE id = ?";
		int availableStock = jdbc.queryForObject(stockSql, Integer.class, variantId);

		// 2. Kiểm tra xem sản phẩm đã có trong giỏ chưa
		String checkSql = "SELECT id, quantity FROM cart_items WHERE user_id = ? AND product_variant_id = ?";
		List<Map<String, Object>> existing = jdbc.queryForList(checkSql, accountId, variantId);

		int currentInCart = 0;
		if (!existing.isEmpty()) {
			currentInCart = ((Number) existing.get(0).get("quantity")).intValue();
		}

		if (currentInCart + quantity > availableStock) {
			response.put("success", false);
			response.put("message", "Xin lỗi, kho chỉ còn " + availableStock + " sản phẩm.");
			return response;
		}

		if (!existing.isEmpty()) {
			// Update quantity
			int newQty = currentInCart + quantity;
			jdbc.update("UPDATE cart_items SET quantity = ? WHERE id = ?", newQty, existing.get(0).get("id"));
		} else {
			// Insert new
			jdbc.update("INSERT INTO cart_items (user_id, product_variant_id, quantity) VALUES (?, ?, ?)",
					accountId, variantId, quantity);
		}

		response.put("success", true);
		response.put("message", "Đã thêm vào giỏ hàng");
		return response;
	}

	@PostMapping("/cart/update")
	@ResponseBody
	public Map<String, Object> updateCart(@RequestParam("itemId") Integer itemId,
			@RequestParam("quantity") Integer quantity,
			HttpSession session) {
		Map<String, Object> response = new HashMap<>();
		@SuppressWarnings("unchecked")
		Map<String, Object> account = (Map<String, Object>) session.getAttribute("account");

		if (account == null) {
			response.put("success", false);
			return response;
		}

		if (quantity <= 0) {
			jdbc.update("DELETE FROM cart_items WHERE id = ?", itemId);
		} else {
			// Kiểm tra tồn kho trước khi update
			String stockSql = "SELECT v.quantity FROM product_variants v " +
					"JOIN cart_items ci ON v.id = ci.product_variant_id " +
					"WHERE ci.id = ?";
			int availableStock = jdbc.queryForObject(stockSql, Integer.class, itemId);

			if (quantity > availableStock) {
				response.put("success", false);
				response.put("message", "Chỉ còn " + availableStock + " sản phẩm trong kho");
				return response;
			}

			jdbc.update("UPDATE cart_items SET quantity = ? WHERE id = ?", quantity, itemId);
		}

		response.put("success", true);
		return response;
	}

	@PostMapping("/cart/remove")
	@ResponseBody
	public Map<String, Object> removeFromCart(@RequestParam("itemId") Integer itemId, HttpSession session) {
		Map<String, Object> response = new HashMap<>();
		@SuppressWarnings("unchecked")
		Map<String, Object> account = (Map<String, Object>) session.getAttribute("account");

		if (account == null) {
			response.put("success", false);
			return response;
		}

		jdbc.update("DELETE FROM cart_items WHERE id = ?", itemId);
		response.put("success", true);
		return response;
	}
}
