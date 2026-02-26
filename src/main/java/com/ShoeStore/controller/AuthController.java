package com.ShoeStore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
public class AuthController {
    
    @Autowired
    private JdbcTemplate jdbc;

    // ----- GET: Hiển thị giao diện -----
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/register")
    public String register() {
        return "register";
    }

    // ----- POST: Xử lý Đăng Nhập -----
    @PostMapping("/login")
    public String processLogin(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            Model model) {

        try {
            // Lấy thông tin tài khoản từ CSDL dựa trên Email
            String sql = "SELECT id, password, role, full_name, status FROM accounts WHERE email = ?";
            Map<String, Object> account = jdbc.queryForMap(sql, email);

            // Kiểm tra mật khẩu (Xếp nhớ: Ở thực tế nên dùng BCrypt để mã hóa)
            String dbPassword = (String) account.get("password");
            
            if (password.equals(dbPassword)) {
                // Kiểm tra xem tài khoản có bị khóa không (status = 0)
                int status = (Integer) account.get("status");
                if (status == 0) {
                    model.addAttribute("error", "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ hỗ trợ!");
                    return "login";
                }

                // Nếu là ADMIN -> chuyển hướng sang trang Dashboard
                String role = (String) account.get("role");
                if ("ADMIN".equalsIgnoreCase(role)) {
                    return "redirect:/admin/dashboard";
                } 
                // Nếu là USER -> chuyển hướng về Trang chủ mua sắm
                else {
                    return "redirect:/"; // Lưu ý: Cần có một Controller mapping với "/"
                }

            } else {
                model.addAttribute("error", "Mật khẩu không chính xác!");
                return "login";
            }

        } catch (EmptyResultDataAccessException e) {
            // Không tìm thấy Email trong database
            model.addAttribute("error", "Email này chưa được đăng ký!");
            return "login";
        } catch (Exception e) {
            // Lỗi hệ thống khác
            model.addAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
            return "login";
        }
    }

    // ----- POST: Xử lý Đăng Ký -----
    @PostMapping("/register")
    public String processRegister(
            @RequestParam("fullName") String fullName,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model) {

        // 1. Kiểm tra 2 mật khẩu
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu nhập lại không khớp!");
            return "register";
        }

        // 2. Kiểm tra trùng Email
        String checkEmailSql = "SELECT COUNT(*) FROM accounts WHERE email = ?";
        Integer count = jdbc.queryForObject(checkEmailSql, Integer.class, email);
        
        if (count != null && count > 0) {
            model.addAttribute("error", "Địa chỉ Email này đã được sử dụng!");
            return "register";
        }

        try {
            // 3. Tạo mã ngẫu nhiên
            String userCode = "U" + (System.currentTimeMillis() % 10000);

            // 4. Lưu vào Database
            String insertSql = "INSERT INTO accounts (user_code, email, password, full_name, role, status, membership_rank_id) " +
                               "VALUES (?, ?, ?, ?, 'USER', 1, 1)";
            jdbc.update(insertSql, userCode, email, password, fullName);

            model.addAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "register";

        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "register";
        }
    }
}