package com.mayhotel.web_khachsan_nhom2.controller;

import com.mayhotel.web_khachsan_nhom2.model.DanhGia;
import com.mayhotel.web_khachsan_nhom2.model.User;
import com.mayhotel.web_khachsan_nhom2.repository.DanhGiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/review")
public class ReviewController extends BaseController {

    @Autowired
    private DanhGiaRepository danhGiaRepository;

    // 1. GIAO DIỆN HIỂN THỊ TRANG ĐÁNH GIÁ (GET)
    @GetMapping("")
    public String index(
            @RequestParam(value = "success_submit", required = false) String successSubmit,
            Model model) {

        setPageTitle(model, "Đánh giá từ khách hàng");

        if (successSubmit != null) {
            model.addAttribute("success", "Gửi đánh giá thành công! Phản hồi của bạn đang chờ Admin kiểm duyệt.");
        }

        // Lọc lấy các bình luận ĐÃ ĐƯỢC ADMIN DUYỆT (trangThai == 1) để đưa lên slide công khai
        List<DanhGia> approvedReviews = danhGiaRepository.findAll().stream()
                .filter(r -> r.getTrangThai() != null && r.getTrangThai() == 1)
                .collect(Collectors.toList());

        model.addAttribute("reviews", approvedReviews);
        return render(model, "view/Home/review");
    }

    // 2. XỬ LÝ KHÁCH HÀNG GỬI ĐÁNH GIÁ MỚI (POST)
    @PostMapping("/submit")
    public String submitReview(
            @RequestParam("soSao") Integer soSao,
            @RequestParam("noiDung") String noiDung,
            HttpSession session,
            Model model) {

        User currentUser = (User) session.getAttribute("user");

        // CHÈN LOG KIỂM TRA: Xem Session có thực sự bị mất hay không
        System.out.println("-> [DEBUG GỬI ĐÁNH GIÁ] Tài khoản đăng nhập trong Session: "
                + (currentUser != null ? currentUser.getTenDangNhap() : "NULL (Trống rỗng)"));

        if (currentUser == null) {
            model.addAttribute("error", "Phiên đăng nhập đã hết hạn hoặc bạn chưa đăng nhập tài khoản khách!");
            setPageTitle(model, "Đánh giá từ khách hàng");

            // Tải lại danh sách phòng tránh trống trang
            List<DanhGia> approvedReviews = danhGiaRepository.findAll().stream()
                    .filter(r -> r.getTrangThai() != null && r.getTrangThai() == 1)
                    .collect(Collectors.toList());
            model.addAttribute("reviews", approvedReviews);
            return render(model, "view/Home/review");
        }

        try {
            DanhGia newReview = new DanhGia();
            newReview.setUser(currentUser);
            newReview.setNoiDung(noiDung);
            newReview.setSoSao(soSao);
            newReview.setTenPhong("Phòng lưu trú MAY HOTEL");
            newReview.setNgayDanhGia(LocalDateTime.now());
            newReview.setTrangThai(0); // Mặc định = 0 (Chờ duyệt)

            // FIX CHÍ MẠNG: Dùng saveAndFlush để ép Java ghi xuống SQL Server ngay tại đây
            danhGiaRepository.saveAndFlush(newReview);

            // Điều hướng an toàn kèm tham số URL
            return "redirect:/review?success_submit";

        } catch (Exception e) {
            // IN TOÀN BỘ VẾT LỖI THỰC TẾ RA CỬA SỔ CONSOLE ĐỂ BẠN PHÁT HIỆN RA THỦ PHẠM
            System.out.println("\n!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.out.println("-> [LỖI HỆ THỐNG] KHÔNG THỂ GHI BẢN GHI ĐÁNH GIÁ XUỐNG SQL SERVER:");
            e.printStackTrace();
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");

            // Giữ người dùng ở lại trang và in trực tiếp thông báo lỗi DB lên màn hình giao diện
            model.addAttribute("error", "Không thể gửi đánh giá do lỗi cấu hình CSDL: " + e.getMessage());
            setPageTitle(model, "Đánh giá từ khách hàng");

            List<DanhGia> approvedReviews = danhGiaRepository.findAll().stream()
                    .filter(r -> r.getTrangThai() != null && r.getTrangThai() == 1)
                    .collect(Collectors.toList());
            model.addAttribute("reviews", approvedReviews);

            return render(model, "view/Home/review");
        }
    }

    protected String render(Model model, String viewPath) {
        model.addAttribute("view", viewPath);
        model.addAttribute("extra_css", viewPath + " :: extra_css");
        model.addAttribute("extra_js", viewPath + " :: extra_js");
        return "layout/layout";
    }
}