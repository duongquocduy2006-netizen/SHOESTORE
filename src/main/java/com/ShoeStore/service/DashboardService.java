package com.ShoeStore.service;

import com.ShoeStore.model.Activity;
import com.ShoeStore.model.RevenueItem;
import com.ShoeStore.model.TopProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    @Autowired
    private JdbcTemplate jdbc;

    // 1. Tổng doanh thu (Đơn hàng status = 1)
    public Double getTotalRevenue() {
        String sql = "SELECT SUM(final_amount) FROM orders WHERE status = 1";
        Double total = jdbc.queryForObject(sql, Double.class);
        return total != null ? total : 0.0;
    }

    // 2. Tổng số đơn hàng mới
    public Integer getNewOrdersCount() {
        String sql = "SELECT COUNT(id) FROM orders";
        Integer count = jdbc.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }

    // 3. Số sản phẩm sắp hết hàng (quantity < 10)
    public Integer getLowStockCount() {
        String sql = "SELECT COUNT(id) FROM product_variants WHERE quantity < 10";
        Integer count = jdbc.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }

    // 4. Khách hàng mới
    public Integer getNewCustomersCount() {
        String sql = "SELECT COUNT(id) FROM accounts WHERE role = 'USER'";
        Integer count = jdbc.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }

    // 5. Lấy danh sách Top Sản phẩm bán chạy (Map vào TopProduct của Xếp)
    public List<TopProduct> getTopProducts() {
        String sql = "SELECT TOP 4 " +
                "p.id, p.product_name, p.product_code, " +
                "(SELECT TOP 1 image_url FROM product_images pi WHERE pi.product_id = p.id AND pi.is_primary = 1) as image_url, " +
                "c.category_name, " +
                "SUM(oi.quantity) as soldCount, " +
                "SUM(oi.quantity * oi.price) as totalRevenue " +
                "FROM products p " +
                "JOIN categories c ON p.category_id = c.id " +
                "JOIN product_variants pv ON pv.product_id = p.id " +
                "JOIN order_items oi ON oi.product_variant_id = pv.id " +
                "JOIN orders o ON oi.order_id = o.id " +
                "WHERE o.status = 1 " +
                "GROUP BY p.id, p.product_name, p.product_code, c.category_name " +
                "ORDER BY soldCount DESC";

        return jdbc.query(sql, (rs, rowNum) -> {
            TopProduct dto = new TopProduct();
            dto.setId(rs.getInt("id"));
            dto.setName(rs.getString("product_name"));
            dto.setSku(rs.getString("product_code"));
            dto.setImage(rs.getString("image_url") != null ? "/images/" + rs.getString("image_url") : "https://via.placeholder.com/40");
            dto.setCategory(rs.getString("category_name"));
            dto.setSoldCount(rs.getInt("soldCount"));
            dto.setTotalRevenue(rs.getDouble("totalRevenue"));
            return dto;
        });
    }

    // 6. Dữ liệu biểu đồ (Map vào RevenueItem của Xếp)
    public List<RevenueItem> getMonthlyStats() {
        List<RevenueItem> stats = new ArrayList<>();
        stats.add(new RevenueItem("T1", 40, 40000000));
        stats.add(new RevenueItem("T2", 65, 65000000));
        stats.add(new RevenueItem("T3", 50, 50000000));
        stats.add(new RevenueItem("T4", 80, 80000000));
        stats.add(new RevenueItem("T5", 45, 45000000));
        stats.add(new RevenueItem("T6", 100, 100000000)); // Cột cao nhất hiển thị màu Neon
        stats.add(new RevenueItem("T7", 70, 70000000));
        return stats;
    }

    // 7. Hoạt động gần đây (Map vào Activity của Xếp)
    public List<Activity> getRecentActivities() {
        List<Activity> activities = new ArrayList<>();
        activities.add(new Activity("success", "<strong class='text-white'>Nguyễn Văn A</strong> vừa đặt đơn hàng #O001", "2 phút trước"));
        activities.add(new Activity("warning", "Cảnh báo: <strong class='text-white'>Nike Air</strong> sắp hết hàng.", "15 phút trước"));
        activities.add(new Activity("info", "<strong class='text-white'>Trần Thị Bích</strong> đã đăng ký thành viên.", "1 giờ trước"));
        return activities;
    }
}