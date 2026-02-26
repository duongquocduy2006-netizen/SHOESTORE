package com.ShoeStore.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ShoeStore.model.Category;
import com.ShoeStore.repository.CategoryRepository;

import java.util.List;

@Controller
@RequestMapping("/admin/categories") 
public class AdminCategoryManager {
	
    @Autowired
    private CategoryRepository categoryRepo;

    // 1. HIỂN THỊ DANH SÁCH TỪ DATABASE
    @GetMapping
    public String index(Model model) {
        // Lấy toàn bộ danh mục từ bảng categories trong DB
        List<Category> categories = categoryRepo.findAll();
        model.addAttribute("list", categories);
        return "admin/categories"; 
    }

    // 2. HIỂN THỊ FORM THÊM MỚI
    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("category", new Category());
        return "admin/add-categories"; 
    }

    // 3. LƯU DANH MỤC MỚI VÀO DATABASE
    @PostMapping("/add")
    public String saveAdd(@ModelAttribute("category") Category category, Model model) {
        // JPA sẽ tự động phát sinh ID (do đã cài @GeneratedValue trong Entity) và lưu vào DB
        categoryRepo.save(category);
        model.addAttribute("message", "Thêm danh mục mới thành công!");
        
        // Reset lại form trống sau khi thêm thành công
        model.addAttribute("category", new Category());
        return "admin/add-categories";
    }

    // 4. HIỂN THỊ FORM CHỈNH SỬA
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Integer id, Model model) {
        // Tìm danh mục trong DB theo ID
        Category category = categoryRepo.findById(id).orElse(null);
        
        if (category != null) {
            model.addAttribute("category", category);
            return "admin/edit-categories";
        } else {
            return "redirect:/admin/categories"; // Nếu không tìm thấy thì quay về trang danh sách
        }
    }

    // 5. CẬP NHẬT DỮ LIỆU VÀO DATABASE
    @PostMapping("/edit/{id}")
    public String saveUpdate(@PathVariable("id") Integer id, @ModelAttribute("category") Category category, Model model) {
        // Đảm bảo ID được giữ nguyên để JPA hiểu là "Cập nhật" chứ không phải "Thêm mới"
        category.setId(id);
        categoryRepo.save(category);
        
        model.addAttribute("message", "Cập nhật danh mục thành công!");
        return "admin/edit-categories";
    }

    // 6. XÓA DANH MỤC KHỎI DATABASE
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id) {
        // Xóa thẳng dòng có ID tương ứng trong CSDL
        categoryRepo.deleteById(id);
        return "redirect:/admin/categories";
    }
}