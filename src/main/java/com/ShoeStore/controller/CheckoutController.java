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

    @GetMapping("/checkout/quick")
    public String quickCheckout(
            @RequestParam("variantId") Long variantId,
            @RequestParam("quantity") Integer quantity,
            HttpSession session) {
        @SuppressWarnings("unchecked")
        Map<String, Object> account = (Map<String, Object>) session.getAttribute("account");
        if (account == null)
            return "redirect:/login";

        // Store quick checkout info in session
        session.setAttribute("quickCheckout", Map.of("variantId", variantId, "quantity", quantity));
        return "redirect:/checkout?mode=quick";
    }

    @GetMapping("/checkout")
    public String checkout(
            @RequestParam(value = "mode", required = false) String mode,
            HttpSession session, Model model) {
        @SuppressWarnings("unchecked")
        Map<String, Object> account = (Map<String, Object>) session.getAttribute("account");
        if (account == null) {
            return "redirect:/login";
        }

        Long accountId = ((Number) account.get("id")).longValue();

        if ("quick".equals(mode)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> quickInfo = (Map<String, Object>) session.getAttribute("quickCheckout");
            if (quickInfo == null)
                return "redirect:/cart";

            populateQuickCheckoutModel(model, (Long) quickInfo.get("variantId"), (Integer) quickInfo.get("quantity"));
        } else {
            populateCheckoutModel(model, accountId);
            session.removeAttribute("quickCheckout"); // Clear quick checkout if user goes back to normal checkout
        }

        model.addAttribute("account", account);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> cartItems = (List<Map<String, Object>>) model.asMap().get("cartItems");
        if (cartItems == null || cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        return "client/checkout";
    }

    private void populateQuickCheckoutModel(Model model, Long variantId, Integer quantity) {
        String sql = "SELECT v.id as variant_id, p.id as product_id, " +
                "p.product_name, s.size_name, col.color_name, v.price, " +
                "(SELECT TOP 1 '/images/' + image_url FROM product_images WHERE product_id = p.id ORDER BY is_primary DESC, id ASC) as image_url "
                +
                "FROM product_variants v " +
                "JOIN products p ON v.product_id = p.id " +
                "JOIN sizes s ON v.size_id = s.id " +
                "JOIN colors col ON v.color_id = col.id " +
                "WHERE v.id = ?";

        Map<String, Object> item = jdbc.queryForMap(sql, variantId);
        item.put("quantity", quantity);
        item.put("id", -1); // Dummy ID for template logic if needed

        List<Map<String, Object>> cartItems = List.of(item);
        double total = ((Number) item.get("price")).doubleValue() * quantity;

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalPrice", total);
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

        @SuppressWarnings("unchecked")
        Map<String, Object> quickInfo = (Map<String, Object>) session.getAttribute("quickCheckout");
        List<Map<String, Object>> items;

        if (quickInfo != null) {
            // Quick Checkout Mode
            String quickSql = "SELECT v.id as variant_id, v.price, v.quantity as stock FROM product_variants v WHERE v.id = ?";
            Map<String, Object> item = jdbc.queryForMap(quickSql, quickInfo.get("variantId"));
            item.put("quantity", quickInfo.get("quantity"));
            items = List.of(item);
        } else {
            // normal cart checkout
            String cartSql = "SELECT ci.product_variant_id as variant_id, ci.quantity, v.price, v.quantity as stock " +
                    "FROM cart_items ci JOIN product_variants v ON ci.product_variant_id = v.id " +
                    "WHERE ci.user_id = ?";
            items = jdbc.queryForList(cartSql, accountId);
        }

        if (items.isEmpty())
            return "redirect:/cart";

        double total = items.stream()
                .mapToDouble(
                        item -> ((Number) item.get("price")).doubleValue() * ((Number) item.get("quantity")).intValue())
                .sum();

        double shipping = total >= 500000 ? 0 : 30000;
        double finalTotal = total + shipping;

        try {
            // 1. Map Payment Method Name to ID
            Integer pmId;
            try {
                pmId = jdbc.queryForObject(
                        "SELECT TOP 1 id FROM payment_methods WHERE method_name LIKE ? OR ? LIKE '%' + method_name + '%'",
                        Integer.class, "%" + paymentMethod + "%", paymentMethod);
            } catch (Exception e) {
                pmId = 1; // Mặc định là COD
            }

            // 2. Insert into addresses to get receiver_address_id
            // Tách address thành các thành phần (tạm thời để ward/district/province trống
            // hoặc regex nếu cần)
            String addressSql = "INSERT INTO addresses (receiving_name, phone_number, street_detail, is_default, user_id) VALUES (?, ?, ?, 0, ?)";
            jdbc.update(addressSql, fullName, phone, address, accountId);

            Long addressId = jdbc.queryForObject("SELECT TOP 1 id FROM addresses WHERE user_id = ? ORDER BY id DESC",
                    Long.class, accountId);

            // Generate Order Code
            String orderCode = "ORD-" + System.currentTimeMillis() / 1000;

            // 3. Tạo Order (Sửa lại đúng cột DB)
            String orderSql = "INSERT INTO orders (order_code, user_id, total_amount, shipping_fee, final_amount, receiver_address_id, payment_method_id, status, created_at) "
                    +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, 1, GETDATE())";

            jdbc.update(orderSql, orderCode, accountId, total, shipping, finalTotal, addressId, pmId);

            Long orderId = jdbc.queryForObject("SELECT TOP 1 id FROM orders WHERE user_id = ? ORDER BY id DESC",
                    Long.class, accountId);

            // 4. Lưu Order Items & Trừ tồn kho
            for (Map<String, Object> item : items) {
                Integer variantId = ((Number) item.get("variant_id")).intValue();
                Integer buyQty = ((Number) item.get("quantity")).intValue();
                Double price = ((Number) item.get("price")).doubleValue();
                Integer currentStock = ((Number) item.get("stock")).intValue();

                jdbc.update(
                        "INSERT INTO order_items (order_id, product_variant_id, quantity, price) VALUES (?, ?, ?, ?)",
                        orderId, variantId, buyQty, price);

                jdbc.update("UPDATE product_variants SET quantity = ? WHERE id = ?", currentStock - buyQty, variantId);
            }

            if (quickInfo != null) {
                session.removeAttribute("quickCheckout");
            } else {
                jdbc.update("DELETE FROM cart_items WHERE user_id = ?", accountId);
            }

            // ĐIỂM SẼ ĐƯỢC CỘNG KHI ADMIN XÁC NHẬN THÀNH CÔNG (Ở OrderService)

            return "redirect:/checkout/success";

        } catch (Exception e) {
            e.printStackTrace();
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
