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
}