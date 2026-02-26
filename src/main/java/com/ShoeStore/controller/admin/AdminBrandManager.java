package com.ShoeStore.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.ShoeStore.model.Brand;
import com.ShoeStore.repository.BrandRepository;
import java.util.List;

@Controller
@RequestMapping("/admin/brands")
public class AdminBrandManager {

    @Autowired
    private BrandRepository brandRepo;

    @GetMapping
    public String index(Model model) {
        List<Brand> brands = brandRepo.findAll();
        model.addAttribute("list", brands);
        return "admin/brands";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("brand", new Brand());
        return "admin/add-brands";
    }

    @PostMapping("/add")
    public String saveAdd(@ModelAttribute("brand") Brand brand, Model model) {
        brandRepo.save(brand);
        model.addAttribute("message", "Thêm thương hiệu mới thành công!");
        model.addAttribute("brand", new Brand());
        return "admin/add-brands";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Integer id, Model model) {
        Brand brand = brandRepo.findById(id).orElse(null);
        if (brand != null) {
            model.addAttribute("brand", brand);
            return "admin/edit-brands";
        }
        return "redirect:/admin/brands";
    }

    @PostMapping("/edit/{id}")
    public String saveUpdate(@PathVariable("id") Integer id, @ModelAttribute("brand") Brand brand, Model model) {
        brand.setId(id);
        brandRepo.save(brand);
        model.addAttribute("message", "Cập nhật thương hiệu thành công!");
        return "admin/edit-brands";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id) {
        brandRepo.deleteById(id);
        return "redirect:/admin/brands";
    }
}
