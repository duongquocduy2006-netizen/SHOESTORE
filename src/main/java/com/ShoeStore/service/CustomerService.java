package com.ShoeStore.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.ShoeStore.model.CustomerDTO;

@Service
public class CustomerService {

    @Autowired
    private JdbcTemplate jdbc;

    public List<CustomerDTO> getAllCustomers() {
        // Query kết hợp 3 bảng, dùng COALESCE để gán 0đ cho khách chưa mua gì
        String sql = "SELECT a.id, a.full_name, a.email, a.phone, a.status, " +
                     "mr.rank_name, " +
                     "COALESCE((SELECT SUM(final_amount) FROM orders o WHERE o.user_id = a.id AND o.status = 1), 0) as total_spent " +
                     "FROM accounts a " +
                     "LEFT JOIN membership_ranks mr ON a.membership_rank_id = mr.id " +
                     "WHERE a.role = 'USER' " +
                     "ORDER BY a.created_at DESC";

        return jdbc.query(sql, (rs, rowNum) -> {
            CustomerDTO dto = new CustomerDTO();
            dto.setId(rs.getInt("id"));
            dto.setFullName(rs.getString("full_name"));
            dto.setEmail(rs.getString("email"));
            dto.setPhone(rs.getString("phone"));
            dto.setStatus(rs.getInt("status"));
            dto.setRankName(rs.getString("rank_name") != null ? rs.getString("rank_name") : "Đồng");
            dto.setTotalSpent(rs.getDouble("total_spent"));
            return dto;
        });
    }
}