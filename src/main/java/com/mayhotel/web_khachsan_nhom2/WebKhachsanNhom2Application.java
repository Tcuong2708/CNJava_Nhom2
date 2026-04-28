package com.mayhotel.web_khachsan_nhom2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class WebKhachsanNhom2Application {
	public static void main(String[] args) {
		SpringApplication.run(WebKhachsanNhom2Application.class, args);
		System.out.println("--- Dự án May Hotel đã khởi động thành công! ---");
		System.out.println("--- Truy cập tại: http://localhost:8080 ---");
	}
}