package com.ShoeStore; // CHÚ Ý: Package gốc của Xếp là cái này nhé!

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShoeStoreJava5AsmApplication {

    public static void main(String[] args) {
        // ĐÒN SÁT THỦ: Ép Tomcat vô hiệu hóa giới hạn số lượng file rác
        System.setProperty("org.apache.tomcat.util.http.fileupload.MAX_FILE_COUNT", "-1");
        
        SpringApplication.run(ShoeStoreJava5AsmApplication.class, args);
    }
}