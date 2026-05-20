package com.mayhotel.web_khachsan_nhom2.controller;

import com.mayhotel.web_khachsan_nhom2.model.DanhGia;
import com.mayhotel.web_khachsan_nhom2.repository.DanhGiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/reviews") // Khớp hoàn toàn với th:href đường dẫn trên thanh Menu Layout
public class AdminReviewController extends BaseController {

    @Autowired
    private DanhGiaRepository danhGiaRepository;

    // 1. TRANG HIỂN THỊ DANH SÁCH ĐÁNH GIÁ KIỂM DUYỆT
    @GetMapping("")
    public String index(Model model) {
        try {
            setPageTitle(model, "Quản lý đánh giá");
            List<DanhGia> reviews = danhGiaRepository.findAll();
            model.addAttribute("reviews", reviews);

            return render(model, "view/Admin/Review/index");
        } catch (Exception e) {
            // Ép in vết lỗi ra Console IntelliJ để chẩn đoán nếu CSDL hoặc file HTML bị lỗi
            System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.out.println("-> [LỖI QUẢN LÝ ĐÁNH GIÁ]: KHÔNG THỂ TẢI TRANG DO:");
            e.printStackTrace();
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");

            model.addAttribute("error", "Hệ thống không thể tải danh sách dữ liệu: " + e.getMessage());
            setPageTitle(model, "Lỗi hệ thống");
            return render(model, "view/Admin/Review/index");
        }
    }

    // 2. PHÊ DUYỆT HIỂN THỊ BÌNH LUẬN CÔNG KHAI
    @GetMapping("/approve/{id}")
    public String approve(@PathVariable("id") Integer id, RedirectAttributes ra) {
        try {
            DanhGia review = danhGiaRepository.findById(id).orElse(null);
            if (review != null) {
                review.setTrangThai(1); // Chuyển trạng thái: 1 tức là Đã duyệt lên trang chủ công khai
                danhGiaRepository.save(review);
                ra.addFlashAttribute("success", "Đã duyệt bình luận hiển thị công khai thành công!");
            } else {
                ra.addFlashAttribute("error", "Đánh giá yêu cầu thao tác không tồn tại trên hệ thống.");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi xảy ra khi phê duyệt dữ liệu: " + e.getMessage());
        }
        return "redirect:/admin/reviews";
    }

    // 3. XÓA BỎ BÌNH LUẬN TRÁI QUY ĐỊNH
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id, RedirectAttributes ra) {
        try {
            if (danhGiaRepository.existsById(id)) {
                danhGiaRepository.deleteById(id);
                ra.addFlashAttribute("success", "Đã xóa bỏ bài đánh giá phản hồi khỏi hệ thống thành công!");
            } else {
                ra.addFlashAttribute("error", "Bài đánh giá không tồn tại.");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Không thể thực thi xóa dữ liệu: " + e.getMessage());
        }
        return "redirect:/admin/reviews";
    }

    // Gắn bộ khung hàm render bóc tách Layout đồng bộ theo dự án của nhóm bạn
    protected String render(Model model, String viewPath) {
        model.addAttribute("view", viewPath);
        model.addAttribute("extra_css", viewPath + " :: extra_css");
        model.addAttribute("extra_js", viewPath + " :: extra_js");
        return "layout/layout";
    }
}