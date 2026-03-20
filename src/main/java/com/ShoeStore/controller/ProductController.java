
package com.ShoeStore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Map;

@Controller
public class ProductController {

    @Autowired
    private JdbcTemplate jdbc;

    @GetMapping("/shop")
    public String viewShop(
            @RequestParam(value = "q", required = false) String keyword,
            @RequestParam(value = "brand", required = false) List<String> brands,
            @RequestParam(value = "category", required = false) List<Integer> categories,
            @RequestParam(value = "min", required = false) Double minPrice,
            @RequestParam(value = "max", required = false) Double maxPrice,
            @RequestParam(value = "sort", required = false, defaultValue = "newest") String sort,
            @RequestParam(value = "page", defaultValue = "0") int page,
            HttpSession session,
            Model model) {

        int pageSize = 9;
        int offset = page * pageSize;

        StringBuilder sql = new StringBuilder(
                "SELECT p.id, p.product_name, p.brand_name, " +
                        "(SELECT TOP 1 '/images/' + image_url FROM product_images WHERE product_id = p.id ORDER BY is_primary DESC, id ASC) as image_url, "
                        +
                        "(SELECT MIN(price) FROM product_variants WHERE product_id = p.id) as min_price " +
                        "FROM products p " +
                        "LEFT JOIN categories c ON p.category_id = c.id " +
                        "WHERE p.status = 1 AND c.status = 1 " +
                        "AND EXISTS (SELECT 1 FROM brands b WHERE b.brand_name = p.brand_name AND b.status = 1) " +
                        "AND EXISTS (SELECT 1 FROM product_variants pv WHERE pv.product_id = p.id AND pv.quantity > 0) ");

        // --- FILTER LOGIC ---
        if (keyword != null && !keyword.trim().isEmpty()) {
            String k = keyword.trim().replace("'", "''");
            sql.append(" AND (p.product_name LIKE N'%").append(k).append("%' ")
                    .append(" OR p.brand_name LIKE N'%").append(k).append("%' ")
                    .append(" OR c.category_name LIKE N'%").append(k).append("%') ");
        }

        if (brands != null && !brands.isEmpty()) {
            sql.append(" AND p.brand_name IN (");
            for (int i = 0; i < brands.size(); i++) {
                sql.append("'").append(brands.get(i).replace("'", "''")).append("'");
                if (i < brands.size() - 1)
                    sql.append(",");
            }
            sql.append(") ");
        }

        if (categories != null && !categories.isEmpty()) {
            sql.append(" AND p.category_id IN (");
            for (int i = 0; i < categories.size(); i++) {
                sql.append(categories.get(i));
                if (i < categories.size() - 1)
                    sql.append(",");
            }
            sql.append(") ");
        }

        if (minPrice != null || maxPrice != null) {
            sql.append(" AND EXISTS (SELECT 1 FROM product_variants pv WHERE pv.product_id = p.id ");
            if (minPrice != null)
                sql.append(" AND pv.price >= ").append(minPrice);
            if (maxPrice != null)
                sql.append(" AND pv.price <= ").append(maxPrice);
            sql.append(") ");
        }

        // --- SORT LOGIC ---
        String orderBy = " ORDER BY p.created_at DESC ";
        if ("price-asc".equals(sort))
            orderBy = " ORDER BY min_price ASC ";
        else if ("price-desc".equals(sort))
            orderBy = " ORDER BY min_price DESC ";

        // --- PAGINATION (SQL Server) ---
        String countSql = "SELECT COUNT(*) FROM (" + sql.toString() + ") as t";
        int totalItems = jdbc.queryForObject(countSql, Integer.class);
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        sql.append(orderBy);
        sql.append(" OFFSET ").append(offset).append(" ROWS FETCH NEXT ").append(pageSize).append(" ROWS ONLY");

        List<Map<String, Object>> products = jdbc.queryForList(sql.toString());

        model.addAttribute("products", products);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentSort", sort);
        model.addAttribute("selectedBrands", brands);
        model.addAttribute("selectedCategories", categories);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("keyword", keyword);

        return "client/shop";
    }

    @GetMapping("/details")
    public String viewProductDetails(@RequestParam("id") Integer id, Model model, HttpSession session) {
        try {
            // 1. Thông tin cơ bản sản phẩm
            String sqlInfo = "SELECT p.*, c.category_name FROM products p " +
                    "LEFT JOIN categories c ON p.category_id = c.id " +
                    "WHERE p.id = ? AND c.status = 1 AND EXISTS (SELECT 1 FROM brands b WHERE b.brand_name = p.brand_name AND b.status = 1)";
            Map<String, Object> product = jdbc.queryForMap(sqlInfo, id);
            System.out.println("DEBUG Product: " + product);
            model.addAttribute("p", product);

            // 2. Danh sách ảnh (thêm tiền tố /images/)
            String sqlImages = "SELECT '/images/' + image_url as image_url, is_primary FROM product_images WHERE product_id = ? ORDER BY is_primary DESC";
            List<Map<String, Object>> images = jdbc.queryForList(sqlImages, id);
            model.addAttribute("images", images);

            // 3. Danh sách biến thể (Size, Color, Price)
            String sqlVariants = "SELECT v.*, s.size_name, cl.color_name FROM product_variants v " +
                    "LEFT JOIN sizes s ON v.size_id = s.id " +
                    "LEFT JOIN colors cl ON v.color_id = cl.id " +
                    "WHERE v.product_id = ? AND v.status = 1";
            List<Map<String, Object>> variants = jdbc.queryForList(sqlVariants, id);
            model.addAttribute("variants", variants);

            // 4. Sản phẩm liên quan
            Object catObj = product.get("category_id");
            Integer categoryId = (catObj instanceof Number) ? ((Number) catObj).intValue() : null;

            String sqlRelated = "SELECT TOP 4 p.id, p.product_name, c.category_name as brand_name, " +
                    "(SELECT TOP 1 '/images/' + image_url FROM product_images WHERE product_id = p.id ORDER BY is_primary DESC) as image_url, "
                    + "(SELECT MIN(price) FROM product_variants WHERE product_id = p.id) as min_price " +
                    "FROM products p " +
                    "LEFT JOIN categories c ON p.category_id = c.id " +
                    "WHERE p.category_id = ? AND p.id <> ? AND p.status = 1 AND c.status = 1 " +
                    "AND EXISTS (SELECT 1 FROM brands b WHERE b.brand_name = p.brand_name AND b.status = 1) " +
                    "AND EXISTS (SELECT 1 FROM product_variants pv WHERE pv.product_id = p.id AND pv.quantity > 0)";

            List<Map<String, Object>> relatedProducts = jdbc.queryForList(sqlRelated, categoryId, id);
            model.addAttribute("relatedProducts", relatedProducts);

            // 5. Lấy danh sách Voucher khả dụng
            @SuppressWarnings("unchecked")
            Map<String, Object> account = (Map<String, Object>) session.getAttribute("account");
            Integer userRankId = (account != null) ? (Integer) account.get("membership_rank_id") : null;

            String sqlVouchers = "SELECT DISTINCT v.* FROM vouchers v " +
                    "LEFT JOIN voucher_membership_ranks vr ON v.id = vr.voucher_id " +
                    "WHERE v.status = 1 AND v.quantity > 0 " +
                    "AND (v.end_date IS NULL OR v.end_date > GETDATE()) " +
                    "AND (v.start_date IS NULL OR v.start_date <= GETDATE())";

            List<Map<String, Object>> vouchers = jdbc.queryForList(sqlVouchers);
            // Nếu có rank, lọc ra những voucher cho phép rank đó HOẶC voucher cho tất cả
            // rank (không có trong voucher_membership_ranks)
            if (userRankId != null) {
                String sqlRankVouchers = sqlVouchers
                        + " AND (NOT EXISTS (SELECT 1 FROM voucher_membership_ranks vr2 WHERE vr2.voucher_id = v.id) " +
                        " OR EXISTS (SELECT 1 FROM voucher_membership_ranks vr3 WHERE vr3.voucher_id = v.id AND vr3.rank_id = ?))";
                vouchers = jdbc.queryForList(sqlRankVouchers, userRankId);
            } else {
                // Nếu chưa đăng nhập, chỉ hiện voucher cho tất cả các hạng
                String sqlPublicVouchers = sqlVouchers
                        + " AND NOT EXISTS (SELECT 1 FROM voucher_membership_ranks vr2 WHERE vr2.voucher_id = v.id)";
                vouchers = jdbc.queryForList(sqlPublicVouchers);
            }
            model.addAttribute("vouchers", vouchers);

            return "client/details";
        } catch (Exception e) {
            System.out.println("Lỗi load chi tiết SP: " + e.getMessage());
            return "redirect:/shop?error=not_found";
        }
    }

    @GetMapping("/flash-sale")
    public String viewFlashSale() {
        return "client/flash-sale";
    }

    @GetMapping("/new-arrivals")
    public String viewNewArrivals(Model model) {
        // Lấy sản phẩm được thêm trong vòng 3 ngày qua
        String sql = "SELECT p.id, p.product_name, c.category_name as brand_name, " +
                "(SELECT TOP 1 '/images/' + image_url FROM product_images WHERE product_id = p.id ORDER BY is_primary DESC, id ASC) as image_url, "
                + "(SELECT MIN(price) FROM product_variants WHERE product_id = p.id) as min_price " +
                "FROM products p " +
                "LEFT JOIN categories c ON p.category_id = c.id " +
                "WHERE p.status = 1 AND c.status = 1 " +
                "AND EXISTS (SELECT 1 FROM brands b WHERE b.brand_name = p.brand_name AND b.status = 1) " +
                "AND p.created_at >= DATEADD(day, -3, GETDATE()) " +
                "AND EXISTS (SELECT 1 FROM product_variants pv WHERE pv.product_id = p.id AND pv.quantity > 0) " +
                "ORDER BY p.created_at DESC";

        List<Map<String, Object>> newProducts = jdbc.queryForList(sql);
        model.addAttribute("newProducts", newProducts);

        return "client/new-arrivals";
    }
}