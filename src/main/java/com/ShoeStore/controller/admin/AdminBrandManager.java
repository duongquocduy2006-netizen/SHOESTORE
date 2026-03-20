package com.ShoeStore.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.ShoeStore.model.Brand;
import com.ShoeStore.repository.BrandRepository;
import com.ShoeStore.repository.ProductRepository;
import java.util.List;

@Controller
@RequestMapping("/admin/brands")
public class AdminBrandManager {

    @Autowired
    private BrandRepository brandRepo;

    @Autowired
    private ProductRepository productRepo;

    @GetMapping
    public String index(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status,
            Model model) {

        Boolean active = null;
        if ("active".equals(status)) {
            active = true;
        } else if ("inactive".equals(status)) {
            active = false;
        }

        String searchKeyword = (keyword != null) ? keyword : "";

        List<Brand> brands = brandRepo.searchBrands(searchKeyword, active);

        model.addAttribute("list", brands);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
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
    public String delete(@PathVariable("id") Integer id, RedirectAttributes ra) {
        Brand brand = brandRepo.findById(id).orElse(null);
        if (brand != null && productRepo.existsByBrandName(brand.getName())) {
            ra.addFlashAttribute("error",
                    "Không thể xóa thương hiệu này vì vẫn còn sản phẩm thuộc thương hiệu " + brand.getName() + "!");
        } else {
            brandRepo.deleteById(id);
            ra.addFlashAttribute("message", "Xóa thương hiệu thành công!");
        }
        return "redirect:/admin/brands";
    }
}
