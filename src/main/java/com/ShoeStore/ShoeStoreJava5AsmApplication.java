package com.ShoeStore; // CHÚ Ý: Package gốc của Xếp là cái này nhé!

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class ShoeStoreJava5AsmApplication {

    public static void main(String[] args) {
        // ĐÒN SÁT THỦ: Ép Tomcat vô hiệu hóa giới hạn số lượng file rác
        System.setProperty("org.apache.tomcat.util.http.fileupload.MAX_FILE_COUNT", "-1");

        SpringApplication.run(ShoeStoreJava5AsmApplication.class, args);
    }

    @Bean
    public CommandLineRunner fixDatabaseSchemaAccounts(JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                jdbcTemplate.execute("ALTER TABLE accounts ALTER COLUMN full_name NVARCHAR(255)");
                System.out.println("Đã tự động fix cột full_name trong DB thành NVARCHAR(255) (Hỗ trợ Tiếng Việt)!");
            } catch (Exception e) {
                System.out.println("Kiểm tra DB: Cột Accounts đã được cấu hình hoặc bảng không tồn tại (An toàn).");
            }
        };
    }
}