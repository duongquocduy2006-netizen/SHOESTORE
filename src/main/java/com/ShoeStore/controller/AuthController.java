package com.ShoeStore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.ShoeStore.model.LoginRequest;
import com.ShoeStore.model.RegisterRequest;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.Map;

@Controller
public class AuthController {

    @Autowired
    private JdbcTemplate jdbc;

    // ----- GET: Hiển thị giao diện -----
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    // ----- POST: Xử lý Đăng Nhập -----
    @PostMapping("/login")
    public String processLogin(
            @Valid @ModelAttribute("loginRequest") LoginRequest loginRequest,
            BindingResult result,
            HttpSession session,
            Model model) {

        if (result.hasErrors()) {
            return "login";
        }

        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        Map<String, Object> account = null;
        try {
            // Lấy thông tin tài khoản từ CSDL dựa trên Email
            String sql = "SELECT id, password, role, full_name, status, email, phone, points, membership_rank_id FROM accounts WHERE email = ?";
            account = jdbc.queryForMap(sql, email);
        } catch (EmptyResultDataAccessException e) {
            // Không tìm thấy Email trong database
            result.rejectValue("email", "error.email", "Email này chưa được đăng ký!");
            return "login";
        } catch (Exception e) {
            // Lỗi hệ thống khác khi query
            model.addAttribute("error", "Đã xảy ra lỗi hệ thống: " + e.getMessage());
            return "login";
        }

        // Kiểm tra mật khẩu
        String dbPassword = (String) account.get("password");

        if (password.equals(dbPassword)) {
            // Kiểm tra xem tài khoản có bị khóa không (status = 0)
            int status = (Integer) account.get("status");
            if (status == 0) {
                result.rejectValue("email", "error.email", "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ hỗ trợ!");
                return "login";
            }

            // Lưu thông tin người dùng vào Session
            session.setAttribute("account", account);

            // Phân quyền
            String role = (String) account.get("role");
            if ("ADMIN".equalsIgnoreCase(role)) {
                return "redirect:/admin/dashboard";
            }
            return "redirect:/"; // Nếu là USER (hoặc null) về trang chủ
        } else {
            result.rejectValue("password", "error.password", "Mật khẩu không chính xác!");
            return "login";
        }
    }

    // ----- POST: Xử lý Đăng Ký -----
    @PostMapping("/register")
    public String processRegister(
            @Valid @ModelAttribute("registerRequest") RegisterRequest registerRequest,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "register";
        }

        String email = registerRequest.getEmail();
        String password = registerRequest.getPassword();
        String fullName = registerRequest.getFullName();
        String confirmPassword = registerRequest.getConfirmPassword();

        // 1. Kiểm tra 2 mật khẩu
        if (!password.equals(confirmPassword)) {
            result.rejectValue("confirmPassword", "error.confirmPassword", "Mật khẩu nhập lại không khớp!");
            return "register";
        }

        // 2. Kiểm tra trùng Email
        String checkEmailSql = "SELECT COUNT(*) FROM accounts WHERE email = ?";
        Integer count = jdbc.queryForObject(checkEmailSql, Integer.class, email);

        if (count != null && count > 0) {
            result.rejectValue("email", "error.email", "Địa chỉ Email này đã được sử dụng!");
            return "register";
        }

        try {
            // 3. Tạo mã ngẫu nhiên
            String userCode = "U" + (System.currentTimeMillis() % 10000);

            // 4. Lưu vào Database
            String insertSql = "INSERT INTO accounts (user_code, email, password, full_name, role, status, membership_rank_id) "
                    +
                    "VALUES (?, ?, ?, ?, 'USER', 1, 1)";
            jdbc.update(insertSql, userCode, email, password, fullName);

            model.addAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "register";

        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "register";
        }
    }

    // ----- GET / POST: Xử lý Đăng Xuất -----
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Hủy bỏ session hiện tại
        session.invalidate();
        return "redirect:/login"; // Redirect về trang đăng nhập
    }

    @PostMapping("/logout")
    public String processLogout(HttpSession session) {
        // Hủy bỏ session hiện tại
        session.invalidate();
        return "redirect:/login"; // Redirect về trang đăng nhập
    }
}
