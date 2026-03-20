package com.ShoeStore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ShoeStore.model.Voucher;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class CheckoutController {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private com.ShoeStore.service.VoucherService voucherService;

    @PostMapping("/checkout/apply-voucher")
    public String applyVoucher(@RequestParam("voucherCode") String code, HttpSession session, RedirectAttributes ra) {
        @SuppressWarnings("unchecked")
        Map<String, Object> account = (Map<String, Object>) session.getAttribute("account");
        if (code == null || code.trim().isEmpty()) {
            session.removeAttribute("appliedVoucher");
            ra.addFlashAttribute("voucherError", "Vui lòng nhập mã giảm giá!");
            return "redirect:/checkout" + (session.getAttribute("quickCheckout") != null ? "?mode=quick" : "");
        }

        // Get actual rank from DB to be sure
        Integer rankId = jdbc.queryForObject("SELECT membership_rank_id FROM accounts WHERE id = ?", Integer.class,
                account.get("id"));

        // Need to calculate current total to validate minOrderValue
        Double total = calculateCartTotal(session, ((Number) account.get("id")).longValue());

        Optional<Voucher> voucherOpt = voucherService.validateVoucher(code, rankId, total,
                ((Number) account.get("id")).longValue());
        if (voucherOpt.isPresent()) {
            session.setAttribute("appliedVoucher", voucherOpt.get());
            ra.addFlashAttribute("voucherSuccess", "Áp dụng mã giảm giá thành công!");
        } else {
            session.removeAttribute("appliedVoucher");
            ra.addFlashAttribute("voucherError", "Mã giảm giá không hợp lệ, hết hạn hoặc không đủ điều kiện!");
        }
        return "redirect:/checkout" + (session.getAttribute("quickCheckout") != null ? "?mode=quick" : "");
    }

    private Double calculateCartTotal(HttpSession session, Long accountId) {
        @SuppressWarnings("unchecked")
        Map<String, Object> quickInfo = (Map<String, Object>) session.getAttribute("quickCheckout");
        if (quickInfo != null) {
            Map<String, Object> item = jdbc.queryForMap("SELECT price FROM product_variants WHERE id = ?",
                    quickInfo.get("variantId"));
            return ((Number) item.get("price")).doubleValue() * ((Number) quickInfo.get("quantity")).intValue();
        } else {
            List<Map<String, Object>> items = jdbc.queryForList(
                    "SELECT ci.quantity, v.price FROM cart_items ci JOIN product_variants v ON ci.product_variant_id = v.id WHERE ci.user_id = ?",
                    accountId);
            return items.stream()
                    .mapToDouble(i -> ((Number) i.get("price")).doubleValue() * ((Number) i.get("quantity")).intValue())
                    .sum();
        }
    }

    @GetMapping("/checkout/quick")
    public String quickCheckout(
            @RequestParam("variantId") Long variantId,
            @RequestParam("quantity") Integer quantity,
            HttpSession session) {
        Map<String, Object> quickInfo = new HashMap<>();
        quickInfo.put("variantId", variantId);
        quickInfo.put("quantity", quantity);
        session.setAttribute("quickCheckout", quickInfo);
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
            session.removeAttribute("quickCheckout");
        }

        // --- Voucher Logic for Display ---
        Double totalPrice = (Double) model.asMap().get("totalPrice");
        Double shippingFee = totalPrice >= 500000 ? 0.0 : 30000.0;
        Double discount = 0.0;

        Voucher voucher = (Voucher) session.getAttribute("appliedVoucher");
        if (voucher != null) {
            discount = voucherService.calculateDiscount(voucher, totalPrice);
            model.addAttribute("appliedVoucherCode", voucher.getCode());
            model.addAttribute("appliedVoucher", voucher);
        }

        model.addAttribute("account", account);
        model.addAttribute("shippingFee", shippingFee);
        model.addAttribute("discount", discount);
        model.addAttribute("finalTotal", totalPrice + shippingFee - discount);

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
        item.put("id", -1);

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
            String quickSql = "SELECT v.id as variant_id, v.price FROM product_variants v WHERE v.id = ?";
            Map<String, Object> item = jdbc.queryForMap(quickSql, quickInfo.get("variantId"));
            item.put("quantity", quickInfo.get("quantity"));
            items = List.of(item);
        } else {
            String cartSql = "SELECT ci.product_variant_id as variant_id, ci.quantity, v.price " +
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

        // --- Apply Voucher Discount ---
        double discount = 0;
        Voucher voucher = (Voucher) session.getAttribute("appliedVoucher");
        if (voucher != null) {
            discount = voucherService.calculateDiscount(voucher, total);
        }

        double finalTotal = total + shipping - discount;

        try {
            Integer pmId;
            try {
                pmId = jdbc.queryForObject(
                        "SELECT TOP 1 id FROM payment_methods WHERE method_name LIKE ? OR ? LIKE '%' + method_name + '%'",
                        Integer.class, "%" + paymentMethod + "%", paymentMethod);
            } catch (Exception e) {
                pmId = 1;
            }

            String addressSql = "INSERT INTO addresses (receiving_name, phone_number, street_detail, is_default, user_id) VALUES (?, ?, ?, 0, ?)";
            jdbc.update(addressSql, fullName, phone, address, accountId);

            Long addressId = jdbc.queryForObject("SELECT TOP 1 id FROM addresses WHERE user_id = ? ORDER BY id DESC",
                    Long.class, accountId);

            String orderCode = "ORD-" + System.currentTimeMillis() / 1000;

            String orderSql = "INSERT INTO orders (order_code, user_id, total_amount, shipping_fee, final_amount, receiver_address_id, payment_method_id, status, created_at) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, 1, GETDATE())";

            jdbc.update(orderSql, orderCode, accountId, total, shipping, finalTotal, addressId, pmId);

            Long orderId = jdbc.queryForObject("SELECT TOP 1 id FROM orders WHERE user_id = ? ORDER BY id DESC",
                    Long.class, accountId);

            for (Map<String, Object> item : items) {
                Integer variantId = ((Number) item.get("variant_id")).intValue();
                Integer buyQty = ((Number) item.get("quantity")).intValue();
                Double price = ((Number) item.get("price")).doubleValue();

                jdbc.update(
                        "INSERT INTO order_items (order_id, product_variant_id, quantity, price) VALUES (?, ?, ?, ?)",
                        orderId, variantId, buyQty, price);
            }

            // --- Post-Order Actions ---
            if (voucher != null) {
                jdbc.update("UPDATE vouchers SET quantity = quantity - 1 WHERE id = ?", voucher.getId());
                jdbc.update("INSERT INTO voucher_usages (voucher_id, user_id, used_at) VALUES (?, ?, GETDATE())",
                        voucher.getId(), accountId);
                session.removeAttribute("appliedVoucher");
            }

            if (quickInfo != null) {
                session.removeAttribute("quickCheckout");
            } else {
                jdbc.update("DELETE FROM cart_items WHERE user_id = ?", accountId);
            }

            return "redirect:/checkout/success";

        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/checkout?error=order_failed";
        }
    }

    @GetMapping("/checkout/success")
    public String success() {
        return "client/checkout-success";
    }
}
