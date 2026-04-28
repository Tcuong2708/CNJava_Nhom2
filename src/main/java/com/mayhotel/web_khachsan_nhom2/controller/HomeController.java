package com.mayhotel.web_khachsan_nhom2.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(Model model) {
        // Giả lập lấy dữ liệu từ DB (Sau này bạn sẽ gọi Service ở đây)
        model.addAttribute("loaiPhongs", new java.util.ArrayList<>());

        return render(model, "index");
    }

    @GetMapping("/review")
    public String review(Model model) {
        return render(model, "review");
    }

    /**
     * Phương thức dùng chung để cấu hình Layout cho May Hotel
     * @param pageName Tên file html trong thư mục view/home/
     */
    private String render(Model model, String pageName) {
        String viewPath = "view/home/" + pageName;

        model.addAttribute("view", viewPath);
        model.addAttribute("extra_css", viewPath + " :: extra_css");

        model.addAttribute("extra_js", viewPath + " :: extra_js");

        return "layout/layout";
    }
}