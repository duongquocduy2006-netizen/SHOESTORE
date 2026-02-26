package com.ShoeStore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
public class CheckoutController {

    @Autowired
    private JdbcTemplate jdbc;

    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model) {
        @SuppressWarnings("unchecked")
        Map<String, Object> account = (Map<String, Object>) session.getAttribute("account");
        if (account == null) {
            return "redirect:/login";
        }

        Long accountId = ((Number) account.get("id")).longValue();
        populateCheckoutModel(model, accountId);
        model.addAttribute("account", account);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> cartItems = (List<Map<String, Object>>) model.asMap().get("cartItems");
        if (cartItems == null || cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        return "client/checkout";
    }

    private void populateCheckoutModel(Model model, Long accountId) {
        String sql = "SELECT ci.id, ci.quantity, ci.product_variant_id as variant_id, p.id as product_id, " +
                "p.product_name, s.size_name, col.color_name, v.price, " +
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
    }

    @PostMapping("/checkout/place-order")
    public String placeOrder(
            @RequestParam("fullName") String fullName,
            @RequestParam("phone") String phone,
            @RequestParam("address") String address,
            @RequestParam("note") String note,
            @RequestParam("paymentMethod") String paymentMethod,
            HttpSession session,
            Model model) {

        @SuppressWarnings("unchecked")
        Map<String, Object> account = (Map<String, Object>) session.getAttribute("account");
        if (account == null)
            return "redirect:/login";

        Long accountId = ((Number) account.get("id")).longValue();

        // 1. Lấy lại giỏ hàng để tính tổng tiền và lưu order_items
        String cartSql = "SELECT ci.product_variant_id as variant_id, ci.quantity, v.price, v.quantity as stock " +
                "FROM cart_items ci JOIN product_variants v ON ci.product_variant_id = v.id " +
                "WHERE ci.user_id = ?";
        List<Map<String, Object>> items = jdbc.queryForList(cartSql, accountId);

        if (items.isEmpty())
            return "redirect:/cart";

        double total = items.stream()
                .mapToDouble(
                        item -> ((Number) item.get("price")).doubleValue() * ((Number) item.get("quantity")).intValue())
                .sum();

        double shipping = total >= 500000 ? 0 : 30000;
        double finalTotal = total + shipping;

        try {
            // 2. Tạo Order
            String orderSql = "INSERT INTO orders (user_id, total_amount, full_name, phone, address, note, payment_method, status) "
                    +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, 0)";
            jdbc.update(orderSql, accountId, finalTotal, fullName, phone, address, note, paymentMethod);

            Long orderId = jdbc.queryForObject("SELECT TOP 1 id FROM orders WHERE user_id = ? ORDER BY id DESC",
                    Long.class, accountId);

            // 3. Lưu Order Items & Trừ tồn kho
            for (Map<String, Object> item : items) {
                Integer variantId = (Integer) item.get("variant_id");
                Integer buyQty = (Integer) item.get("quantity");
                Double price = ((Number) item.get("price")).doubleValue();
                Integer currentStock = (Integer) item.get("stock");

                jdbc.update(
                        "INSERT INTO order_items (order_id, product_variant_id, quantity, price) VALUES (?, ?, ?, ?)",
                        orderId, variantId, buyQty, price);

                jdbc.update("UPDATE product_variants SET quantity = ? WHERE id = ?", currentStock - buyQty, variantId);
            }

            jdbc.update("DELETE FROM cart_items WHERE user_id = ?", accountId);

            // 5. Cập nhật Điểm và Hạng thành viên (1000đ = 1 điểm)
            int earnedPoints = (int) (finalTotal / 1000);
            jdbc.update("UPDATE accounts SET points = ISNULL(points, 0) + ? WHERE id = ?", earnedPoints, accountId);

            // Lấy lại tổng điểm mới để xét hạng
            Integer totalPoints = jdbc.queryForObject("SELECT points FROM accounts WHERE id = ?", Integer.class,
                    accountId);
            if (totalPoints != null) {
                int newRankId = 1; // Mặc định là Đồng (ID=1)
                if (totalPoints >= 10000) {
                    newRankId = 3; // Vàng (ID=3)
                } else if (totalPoints >= 5000) {
                    newRankId = 2; // Bạc (ID=2)
                }
                jdbc.update("UPDATE accounts SET membership_rank_id = ? WHERE id = ?", newRankId, accountId);
            }

            return "redirect:/checkout/success";

        } catch (Exception e) {
            populateCheckoutModel(model, accountId);
            model.addAttribute("account", account);
            model.addAttribute("error", "Lỗi đặt hàng: " + e.getMessage());
            return "client/checkout";
        }
    }

    @GetMapping("/checkout/success")
    public String success() {
        return "client/checkout-success";
    }
}
