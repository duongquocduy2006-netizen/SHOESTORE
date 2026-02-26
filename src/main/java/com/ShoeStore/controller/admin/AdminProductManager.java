package com.ShoeStore.controller.admin;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ShoeStore.model.Product;
import com.ShoeStore.repository.CategoryRepository;
// NHỚ IMPORT CATEGORY REPOSITORY CỦA XẾP VÀO ĐÂY NHÉ
import com.ShoeStore.repository.ProductRepository;

import java.io.FileOutputStream;
import java.math.BigDecimal;

import com.ShoeStore.repository.SizeRepository;
import com.ShoeStore.repository.ColorRepository;
import com.ShoeStore.repository.ProductImageRepository;
import com.ShoeStore.repository.ProductVariantRepository;
import com.ShoeStore.model.Size;
import com.ShoeStore.model.Color;
import com.ShoeStore.model.ProductImage;
import com.ShoeStore.model.ProductVariant;
import java.util.Base64;
import java.io.File;

@Controller
@RequestMapping("/admin")
public class AdminProductManager {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private ColorRepository colorRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    private void seedData() {
        if (colorRepository.findAll().isEmpty()) {
            String[] colorNames = { "Tr\u1eafng (White)", "\u0110en (Black)", "\u0110\u1ecf (Red)",
                    "Xanh d\u01b0\u01a1ng (Blue)", "X\u00e1m (Grey)",
                    "Be (Beige)", "Cam (Orange)", "V\u00e0ng (Yellow)", "Xanh l\u00e1 (Green)" };
            for (String c : colorNames) {
                Color color = new Color();
                color.setColorName(c);
                colorRepository.save(color);
            }
        }
        if (sizeRepository.findAll().isEmpty()) {
            String[] sizeNames = { "Size 36", "Size 37", "Size 38", "Size 39", "Size 40", "Size 41", "Size 42",
                    "Size 43", "Size 44" };
            for (String s : sizeNames) {
                Size size = new Size();
                size.setSizeName(s);
                sizeRepository.save(size);
            }
        }
    }

    @GetMapping("/products")
    public String products(Model model) {
        List<Product> listProducts = productRepository.findAll();
        model.addAttribute("products", listProducts);
        return "admin/products";
    }

    // Cập nhật hàm Create để đẩy danh sách Category sang
    @GetMapping("/products/create")
    public String showCreateForm(Model model) {
        seedData();
        model.addAttribute("product", new Product());
        // Lấy danh sách danh mục từ DB đẩy sang Form
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("sizes", sizeRepository.findAll());
        model.addAttribute("colors", colorRepository.findAll());
        return "admin/product-form";
    }

    @PostMapping("/products/save")
    public String saveProduct(
            @ModelAttribute("product") Product product,
            @RequestParam(value = "imageBase64", required = false) String imageBase64,
            @RequestParam(value = "sizeId", required = false) Integer sizeId,
            @RequestParam(value = "colorId", required = false) Integer colorId,
            @RequestParam(value = "newColorName", required = false) String newColorName,
            @RequestParam(value = "price", required = false) BigDecimal price,
            @RequestParam(value = "quantity", required = false) Integer quantity,
            @RequestParam(value = "brandName", required = false) String brandName) {

        if (brandName != null && !brandName.isEmpty()) {
            product.setBrandName(brandName);
        }

        // 1. Sinh mã SKU tự động
        if (product.getProductCode() == null || product.getProductCode().isEmpty()) {
            product.setProductCode("PROD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        // Cài đặt trạng thái mặc định nếu null
        if (product.getStatus() == null) {
            product.setStatus(1); // 1 = Đang bán
        }

        // 2. Lưu sản phẩm vào DB
        Product savedProduct = productRepository.save(product);

        // 3. Xử lý lưu ảnh Base64
        if (imageBase64 != null && !imageBase64.isEmpty()) {
            try {
                // Tách phần data:image/png;base64, ra khỏi chuỗi
                String base64Data = imageBase64.contains(",") ? imageBase64.split(",")[1] : imageBase64;
                byte[] decodedBytes = Base64.getDecoder().decode(base64Data);

                // Tạo tên file ngẫu nhiên
                String fileName = UUID.randomUUID().toString() + ".jpg";

                // Đường dẫn lưu file vào thư mục static/images
                // Lưu ý: Trong môi trường thực tế, nên lưu ra thư mục ngoài project hoặc dùng
                // Cloud Storage
                // Ở đây em lưu tạm vào thư mục target/classes/static/images để hiển thị form
                // ngay lập tức
                // Khi restart server, file trong target sẽ bị xóa, Xếp nên dùng đường dẫn tuyệt
                // đối ra ổ đĩa
                String uploadDir = "src/main/resources/static/images/";
                File dir = new File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                String filePath = uploadDir + fileName;
                try (FileOutputStream fos = new FileOutputStream(filePath)) {
                    fos.write(decodedBytes);
                }

                // Lưu thông tin ảnh vào DB
                ProductImage productImage = new ProductImage();
                productImage.setProduct(savedProduct);
                productImage.setImageUrl(fileName); // Tương ứng với th:src ở products.html
                productImage.setIsPrimary(true);
                productImageRepository.save(productImage);

            } catch (Exception e) {
                System.err.println("LỖI LƯU ẢNH: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // 4. Xử lý lưu Biến thể (Size, Color, Price, Quantity)

        // Handle new dynamic color
        if (newColorName != null && !newColorName.trim().isEmpty()) {
            Color newColor = new Color();
            newColor.setColorName(newColorName.trim());
            colorRepository.save(newColor);
            colorId = newColor.getId(); // Use the newly created color ID
        }

        if (sizeId != null && colorId != null && price != null && quantity != null) {
            try {
                Size size = sizeRepository.findById(sizeId).orElse(null);
                Color color = colorRepository.findById(colorId).orElse(null);

                if (size != null && color != null) {
                    ProductVariant variant = null;
                    if (savedProduct.getVariants() != null && !savedProduct.getVariants().isEmpty()) {
                        variant = savedProduct.getVariants().iterator().next();
                    } else {
                        variant = new ProductVariant();
                    }
                    variant.setProduct(savedProduct);
                    variant.setSize(size);
                    variant.setColor(color);
                    variant.setPrice(price);
                    variant.setQuantity(quantity);
                    variant.setStatus(1);

                    productVariantRepository.save(variant);
                }
            } catch (Exception e) {
                System.err.println("LỖI LƯU BIẾN THỂ: " + e.getMessage());
            }
        }

        return "redirect:/admin/products";
    }

    // Cập nhật hàm Edit để đẩy danh sách Category sang
    @GetMapping("/products/edit/{id}")
    public String showEditForm(@PathVariable("id") Integer id, Model model) {
        seedData();
        Product product = productRepository.findById(id).orElse(null);
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryRepository.findAll()); // Thêm dòng này
        model.addAttribute("sizes", sizeRepository.findAll());
        model.addAttribute("colors", colorRepository.findAll());
        return "admin/product-form";
    }

    @GetMapping("/products/detail/{id}")
    public String productDetails(@PathVariable("id") Integer id, Model model) {
        Product product = productRepository.findById(id).orElse(null);
        model.addAttribute("product", product);
        return "admin/product-details";
    }

    @Transactional
    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") Integer id) {
        try {
            // THỨ TỰ XÓA "SẠCH SẼ" NHƯ SAU:

            // Bước 1: Xóa các thứ liên quan đến Biến thể (Variants)
            productRepository.deleteRelatedCartItems(id);
            productRepository.deleteRelatedOrderItems(id);

            // Bước 2: Xóa các thứ liên quan trực tiếp đến Sản phẩm (Product)
            productRepository.deleteRelatedFavourites(id); // EM VỪA THÊM DÒNG NÀY Ở ĐÂY
            productRepository.deleteRelatedReviews(id); // THÊM DÒNG NÀY Ở ĐÂY XẾP ƠI
            productRepository.deleteRelatedImages(id);
            productRepository.deleteRelatedVariants(id);

            // Bước 3: Cuối cùng mới xóa "Gốc" sản phẩm
            productRepository.deleteById(id);

            System.out.println("--- ĐÃ XÓA SẠCH BÁCH SẢN PHẨM ID: " + id);
        } catch (Exception e) {
            System.err.println("LỖI XÓA: " + e.getMessage());
        }

        return "redirect:/admin/products";
    }

    @Transactional
    @GetMapping("/products/image/delete/{imageId}/{productId}")
    public String deleteImage(@PathVariable("imageId") Integer imageId, @PathVariable("productId") Integer productId) {
        try {
            productImageRepository.deleteById(imageId);
        } catch (Exception e) {
            System.err.println("LỖI XÓA ẢNH: " + e.getMessage());
        }
        return "redirect:/admin/products/edit/" + productId;
    }

    @Transactional
    @GetMapping("/products/image/primary/{imageId}/{productId}")
    public String setPrimaryImage(@PathVariable("imageId") Integer imageId,
            @PathVariable("productId") Integer productId) {
        try {
            Product product = productRepository.findById(productId).orElse(null);
            if (product != null && product.getImages() != null) {
                for (ProductImage img : product.getImages()) {
                    img.setIsPrimary(img.getId().equals(imageId));
                    productImageRepository.save(img);
                }
            }
        } catch (Exception e) {
            System.err.println("LỖI SET ẢNH CHÍNH: " + e.getMessage());
        }
        return "redirect:/admin/products/edit/" + productId;
    }
}