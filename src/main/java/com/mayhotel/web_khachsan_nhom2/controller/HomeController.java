package com.mayhotel.web_khachsan_nhom2.controller;

import com.mayhotel.web_khachsan_nhom2.model.LoaiPhong;
import com.mayhotel.web_khachsan_nhom2.model.Phong;
import com.mayhotel.web_khachsan_nhom2.repository.PhongRepository;
import com.mayhotel.web_khachsan_nhom2.repository.LoaiPhongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class HomeController  {

    @Autowired
    private PhongRepository phongRepository;

    @Autowired
    private LoaiPhongRepository loaiPhongRepository;

    @GetMapping("/")
    public String index(Model model) {
        List<LoaiPhong> loaiPhongs = loaiPhongRepository.findAll();

        model.addAttribute("loaiPhongs", loaiPhongs);
        return render(model, "index");
    }

    @GetMapping("/review")
    public String review(Model model) {
        return render(model, "review");
    }

    @GetMapping("/info")
    public String info(Model model){
        return render(model, "info");
    }

    @GetMapping("/home/detail/{id}")
    public String detail(@PathVariable("id") Integer id, Model model) {
        Phong phong = phongRepository.findById(id).orElse(null);
        if (phong == null) {
            return "redirect:/";
        }
        model.addAttribute("phong", phong);
        model.addAttribute("conPhong", phong.getMaTrangThai() == 1);
        model.addAttribute("pageTitle", "Chi tiết phòng " + phong.getName());
        return render(model, "detail");
    }
    /**
     * Phương thức dùng chung để cấu hình Layout cho May Hotel
     * @param pageName Tên file html trong thư mục view/home/
     */
    private String render(Model model, String pageName) {
        String viewPath = "view/Home/" + pageName;

        model.addAttribute("view", viewPath);
        model.addAttribute("extra_css", viewPath + " :: extra_css");
        model.addAttribute("extra_js", viewPath + " :: extra_js");

        return "layout/layout";
    }
}