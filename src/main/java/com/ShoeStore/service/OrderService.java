package com.ShoeStore.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.ShoeStore.model.OrderDTO;

@Service
public class OrderService {

    @Autowired
    private JdbcTemplate jdbc;

    public List<OrderDTO> getAllOrders() {
        // Query kết nối 3 bảng để lấy thông tin chi tiết đơn hàng
        String sql = "SELECT o.order_code, a.receiving_name, o.created_at, o.final_amount, o.status, pm.method_name " +
                "FROM orders o " +
                "LEFT JOIN addresses a ON o.receiver_address_id = a.id " +
                "LEFT JOIN payment_methods pm ON o.payment_method_id = pm.id " +
                "ORDER BY o.created_at DESC";

        return jdbc.query(sql, (rs, rowNum) -> {
            OrderDTO dto = new OrderDTO();
            dto.setOrderCode(rs.getString("order_code"));
            dto.setCustomerName(rs.getString("receiving_name"));
            dto.setCreatedAt(rs.getTimestamp("created_at"));
            dto.setFinalAmount(rs.getDouble("final_amount"));
            dto.setStatus(rs.getInt("status"));
            dto.setPaymentMethod(rs.getString("method_name"));
            return dto;
        });
    }

    public void updateOrderStatus(String orderCode, int newStatus) {
        // 1. Lấy trạng thái cũ và thông tin đơn hàng trước khi update
        String checkSql = "SELECT status, user_id, final_amount FROM orders WHERE order_code = ?";
        java.util.Map<String, Object> order = jdbc.queryForMap(checkSql, orderCode);
        int oldStatus = ((Number) order.get("status")).intValue();
        Long userId = ((Number) order.get("user_id")).longValue();
        double finalAmount = ((Number) order.get("final_amount")).doubleValue();

        // 2. Cập nhật trạng thái mới
        String sql = "UPDATE orders SET status = ? WHERE order_code = ?";
        jdbc.update(sql, newStatus, orderCode);

        // 3. Nếu chuyển sang trạng thái "Thành công" (3) và trước đó chưa thành công
        if (newStatus == 3 && oldStatus != 3) {
            int earnedPoints = (int) (finalAmount / 1000);

            // Cộng điểm cho User
            jdbc.update("UPDATE accounts SET points = ISNULL(points, 0) + ? WHERE id = ?", earnedPoints, userId);

            // Xét lại hạng thành viên dựa trên DB
            Integer totalPoints = jdbc.queryForObject("SELECT points FROM accounts WHERE id = ?", Integer.class,
                    userId);
            if (totalPoints != null) {
                // Lấy danh sách hạng từ DB, sắp xếp theo điểm giảm dần
                List<java.util.Map<String, Object>> ranks = jdbc.queryForList(
                        "SELECT id, min_points FROM membership_ranks ORDER BY min_points DESC");

                int newRankId = 1; // Default (thường là hạng thấp nhất)
                if (!ranks.isEmpty()) {
                    for (java.util.Map<String, Object> r : ranks) {
                        int minPoints = ((Number) r.get("min_points")).intValue();
                        if (totalPoints >= minPoints) {
                            newRankId = ((Number) r.get("id")).intValue();
                            break; // Tìm thấy hạng cao nhất thỏa mãn
                        }
                    }
                }

                jdbc.update("UPDATE accounts SET membership_rank_id = ? WHERE id = ?", newRankId, userId);
            }
        }
    }

    public List<java.util.Map<String, Object>> getOrdersByUserId(Long userId) {
        String sql = "SELECT o.id, o.order_code, o.created_at, o.final_amount, o.status, " +
                "(SELECT TOP 1 p.product_name " +
                " FROM order_items oi " +
                " JOIN product_variants pv ON oi.product_variant_id = pv.id " +
                " JOIN products p ON pv.product_id = p.id " +
                " WHERE oi.order_id = o.id) as first_product_name, " +
                "(SELECT TOP 1 pi.image_url " +
                " FROM order_items oi " +
                " JOIN product_variants pv ON oi.product_variant_id = pv.id " +
                " JOIN product_images pi ON pv.product_id = pi.product_id " +
                " WHERE oi.order_id = o.id " +
                " ORDER BY pi.is_primary DESC, pi.id ASC) as first_product_image, " +
                "(SELECT SUM(quantity) FROM order_items WHERE order_id = o.id) as total_items " +
                "FROM orders o " +
                "WHERE o.user_id = ? " +
                "ORDER BY o.created_at DESC";
        return jdbc.queryForList(sql, userId);
    }

    public void confirmOrder(String orderCode, Long userId) {
        // 1. Kiểm tra đơn hàng có thuộc về User này không và đang ở trạng thái Shipping
        // (2)
        String checkSql = "SELECT status, user_id FROM orders WHERE order_code = ?";
        java.util.Map<String, Object> order = jdbc.queryForMap(checkSql, orderCode);

        int currentStatus = ((Number) order.get("status")).intValue();
        Long ownerId = ((Number) order.get("user_id")).longValue();

        if (!ownerId.equals(userId)) {
            throw new RuntimeException("Bạn không có quyền xác nhận đơn hàng này.");
        }

        if (currentStatus != 2) {
            throw new RuntimeException("Chỉ có thể xác nhận khi đơn hàng đang ở trạng thái 'Đang giao'.");
        }

        // 2. Chuyển sang trạng thái Đã nhận hàng - Chờ duyệt (5)
        // Lưu ý: Chưa cộng điểm ở bước này. Admin sẽ duyệt sang 3 mới cộng điểm.
        updateOrderStatus(orderCode, 5);
    }

    public int getOrderStatus(String orderCode) {
        String sql = "SELECT status FROM orders WHERE order_code = ?";
        return jdbc.queryForObject(sql, Integer.class, orderCode);
    }

    public void cancelOrder(String orderCode, Long userId) {
        // 1. Kiểm tra đơn hàng thuộc về User and đang ở trạng thái 'Chờ duyệt' (1)
        String checkSql = "SELECT status, user_id FROM orders WHERE order_code = ?";
        java.util.Map<String, Object> order = jdbc.queryForMap(checkSql, orderCode);

        int currentStatus = ((Number) order.get("status")).intValue();
        Long ownerId = ((Number) order.get("user_id")).longValue();

        if (!ownerId.equals(userId)) {
            throw new RuntimeException("Bạn không có quyền hủy đơn hàng này.");
        }

        if (currentStatus != 1) {
            throw new RuntimeException("Chỉ có thể hủy đơn hàng khi đang ở trạng thái 'Chờ duyệt'.");
        }

        // 2. Chuyển sang trạng thái Đã hủy (4)
        updateOrderStatus(orderCode, 4);
    }

    public java.util.Map<String, Object> getOrderDetail(String orderCode) {
        String sql = "SELECT o.*, a.receiving_name, a.phone_number, a.street_detail, pm.method_name " +
                "FROM orders o " +
                "LEFT JOIN addresses a ON o.receiver_address_id = a.id " +
                "LEFT JOIN payment_methods pm ON o.payment_method_id = pm.id " +
                "WHERE o.order_code = ?";
        return jdbc.queryForMap(sql, orderCode);
    }

    public List<java.util.Map<String, Object>> getOrderItems(String orderCode) {
        String sql = "SELECT oi.quantity, oi.price, p.product_name, s.size_name, col.color_name, " +
                "(SELECT TOP 1 pi.image_url FROM product_images pi WHERE pi.product_id = p.id ORDER BY pi.is_primary DESC, pi.id ASC) as image_url "
                +
                "FROM order_items oi " +
                "JOIN orders o ON oi.order_id = o.id " +
                "JOIN product_variants pv ON oi.product_variant_id = pv.id " +
                "JOIN products p ON pv.product_id = p.id " +
                "JOIN sizes s ON pv.size_id = s.id " +
                "JOIN colors col ON pv.color_id = col.id " +
                "WHERE o.order_code = ?";
        return jdbc.queryForList(sql, orderCode);
    }
}