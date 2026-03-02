package com.ShoeStore.controller.admin;

import com.ShoeStore.service.CustomerService;
import com.ShoeStore.repository.MembershipRankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminCustomerManager {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private MembershipRankRepository rankRepo;

    @GetMapping("/admin/customers")
    public String dashBoard(Model model) {
        // Đẩy danh sách khách hàng sang giao diện Thymeleaf
        model.addAttribute("customers", customerService.getAllCustomers());
        model.addAttribute("ranks", rankRepo.findAll());
        return "admin/customers";
    }

    @PostMapping("/admin/customers/update-rank")
    public String updateRank(@RequestParam("userId") Integer userId,
            @RequestParam("rankId") Integer rankId,
            RedirectAttributes ra) {
        customerService.updateCustomerRank(userId, rankId);
        ra.addFlashAttribute("message", "Cập nhật hạng khách hàng thành công!");
        return "redirect:/admin/customers";
    }
}