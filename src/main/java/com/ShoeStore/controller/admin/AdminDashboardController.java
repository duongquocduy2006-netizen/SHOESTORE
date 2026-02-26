package com.ShoeStore.controller.admin;

import com.ShoeStore.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model; 
import org.springframework.web.bind.annotation.GetMapping;

import java.time.Year;

@Controller
public class AdminDashboardController {

    // Tiêm DashboardService vào để lấy dữ liệu từ Database
    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/admin/dashboard")
    public String dashBoard(Model model) { 
        
        // 1. Cập nhật 4 thẻ chỉ số tổng quan bằng dữ liệu thật từ SQL
        model.addAttribute("totalRevenue", dashboardService.getTotalRevenue());
        model.addAttribute("newOrdersCount", dashboardService.getNewOrdersCount());
        model.addAttribute("lowStockCount", dashboardService.getLowStockCount());
        model.addAttribute("newCustomersCount", dashboardService.getNewCustomersCount());
        
        // Cập nhật mốc thời gian linh động theo năm hiện tại
        model.addAttribute("currentTimeRange", "Tháng này");
        model.addAttribute("currentYear", Year.now().getValue());

        // 2. BƠM THÊM 3 DANH SÁCH NÀY ĐỂ GIAO DIỆN CÓ THỂ HIỂN THỊ (QUAN TRỌNG)
        // Nếu thiếu 3 dòng này, biểu đồ và bảng sản phẩm sẽ báo "Chưa có dữ liệu"
        model.addAttribute("monthlyStats", dashboardService.getMonthlyStats());
        model.addAttribute("recentActivities", dashboardService.getRecentActivities());
        model.addAttribute("topProducts", dashboardService.getTopProducts());

        return "admin/dashboard";
    }
}