package com.ShoeStore.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class MembershipController {

    @Autowired
    private JdbcTemplate jdbc;

    @GetMapping("/membership")
    public String member(HttpSession session, Model model) {
        @SuppressWarnings("unchecked")
        Map<String, Object> account = (Map<String, Object>) session.getAttribute("account");
        if (account == null) {
            return "redirect:/login";
        }

        try {
            // Fetch fresh data from DB to get latest points and rank
            String sql = "SELECT a.*, mr.rank_name FROM accounts a " +
                    "LEFT JOIN membership_ranks mr ON a.membership_rank_id = mr.id " +
                    "WHERE a.id = ?";
            Map<String, Object> fullAccount = jdbc.queryForMap(sql, account.get("id"));

            model.addAttribute("account", fullAccount);
        } catch (Exception e) {
            // Fallback to session account if DB query fails for some reason
            model.addAttribute("account", account);
        }

        return "client/membership";
    }
}
