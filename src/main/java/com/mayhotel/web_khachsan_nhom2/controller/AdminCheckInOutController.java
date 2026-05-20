package com.mayhotel.web_khachsan_nhom2.controller;

import com.mayhotel.web_khachsan_nhom2.model.PhieuDat;
import com.mayhotel.web_khachsan_nhom2.model.Phong;
import com.mayhotel.web_khachsan_nhom2.repository.PhieuDatRepository;
import com.mayhotel.web_khachsan_nhom2.repository.PhongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminCheckInOutController extends BaseController {

    @Autowired
    private PhieuDatRepository phieuDatRepository;

    @Autowired
    private PhongRepository phongRepository;

    // =========================================================================
    // XỬ LÝ NHẬN PHÒNG (CHECK-IN)
    // =========================================================================

    // Hiển thị danh sách phiếu CHỜ nhận phòng
    @GetMapping("/checkin")
    public String showCheckInPage(Model model) {
        try {
            setPageTitle(model, "Nghiệp vụ Nhận phòng - Check-in");

            List<PhieuDat> checkInList = phieuDatRepository.findAll().stream()
                    .filter(p -> (p.getDaThanhToan() == null || p.getDaThanhToan() == false)
                            && (p.getGhiChu() == null || !p.getGhiChu().contains("Check-in")))
                    .collect(Collectors.toList());

            model.addAttribute("invoices", checkInList);
            return render(model, "view/Admin/CheckIn/index");
        } catch (Exception e) {
            model.addAttribute("error", "Không thể tải danh sách check-in: " + e.getMessage());
            return render(model, "view/Admin/CheckIn/index");
        }
    }

    // Thực thi lệnh xác nhận Check-in
    @GetMapping("/checkin/execute/{id}")
    public String executeCheckIn(@PathVariable("id") Integer id, RedirectAttributes ra) {
        try {
            Optional<PhieuDat> phieuOpt = phieuDatRepository.findById(id);
            if (phieuOpt.isPresent()) {
                PhieuDat phieu = phieuOpt.get();

                String ghiChuGoc = phieu.getGhiChu();
                if (ghiChuGoc == null || ghiChuGoc.trim().isEmpty() || ghiChuGoc.trim().equalsIgnoreCase("không")) {
                    phieu.setGhiChu("[STAFF] Đã làm thủ tục Check-in nhận phòng.");
                } else {
                    phieu.setGhiChu(ghiChuGoc.trim() + " | [STAFF] Đã làm thủ tục Check-in nhận phòng.");
                }

                phieuDatRepository.saveAndFlush(phieu);

                ra.addFlashAttribute("success", "Xác nhận Check-in nhận phòng thành công cho khách: " + phieu.getHoTen());
            } else {
                ra.addFlashAttribute("error", "Không tìm thấy mã hóa đơn đặt phòng.");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Thất bại khi làm thủ tục Check-in: " + e.getMessage());
        }
        return "redirect:/admin/checkin";
    }

    // =========================================================================
    // XỬ LÝ TRẢ PHÒNG (CHECK-OUT)
    // =========================================================================

    // Hiển thị danh sách phòng ĐANG Ở để làm thủ tục trả phòng
    @GetMapping("/checkout-room")
    public String showCheckOutPage(Model model) {
        try {
            setPageTitle(model, "Nghiệp vụ Trả phòng - Check-out");

            List<PhieuDat> activeBookings = phieuDatRepository.findAll().stream()
                    .filter(p -> p.getMaPhong() != null
                            && (p.getDaThanhToan() == null || p.getDaThanhToan() == false)
                            && (p.getGhiChu() != null && p.getGhiChu().contains("Check-in")))
                    .collect(Collectors.toList());

            model.addAttribute("invoices", activeBookings);
            return render(model, "view/Admin/CheckOut/index");
        } catch (Exception e) {
            model.addAttribute("error", "Không thể tải danh sách dữ liệu lưu trú: " + e.getMessage());
            return render(model, "view/Admin/CheckOut/index");
        }
    }

    // Thực thi lệnh Check-out kết toán
    @PostMapping("/checkout-room/execute")
    public String executeCheckOut(
            @RequestParam("maHD") Integer maHD,
            @RequestParam(value = "phuThu", required = false) BigDecimal phuThuInput,
            RedirectAttributes ra) {
        try {
            Optional<PhieuDat> phieuOpt = phieuDatRepository.findById(maHD);
            if (phieuOpt.isPresent()) {
                PhieuDat phieu = phieuOpt.get();

                BigDecimal phuThu = (phuThuInput != null) ? phuThuInput : BigDecimal.ZERO;
                phieu.setPhuThu(phuThu);
                phieu.setTotalPrice(phieu.getTotalPrice().add(phuThu));

                // Đánh dấu đã thanh toán -> Thỏa mãn điều kiện biến mất khỏi màn hình Check-out hiện tại
                phieu.setDaThanhToan(true);

                String ghiChuGoc = phieu.getGhiChu();
                if (ghiChuGoc == null || ghiChuGoc.trim().isEmpty() || ghiChuGoc.trim().equalsIgnoreCase("không")) {
                    phieu.setGhiChu("[STAFF] Đã làm thủ tục Check-out bàn giao phòng.");
                } else {
                    phieu.setGhiChu(ghiChuGoc.trim() + " | [STAFF] Đã làm thủ tục Check-out bàn giao phòng.");
                }

                phieuDatRepository.saveAndFlush(phieu);

                if (phieu.getMaPhong() != null) {
                    Optional<Phong> phongOpt = phongRepository.findById(phieu.getMaPhong());
                    if (phongOpt.isPresent()) {
                        Phong phong = phongOpt.get();
                        phong.setMaTrangThai(1);
                        phongRepository.saveAndFlush(phong);
                    }
                }

                ra.addFlashAttribute("success", "Làm thủ tục Check-out & Kết toán hóa đơn #" + maHD + " thành công!");
            } else {
                ra.addFlashAttribute("error", "Hóa đơn xử lý không tồn tại.");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi xảy ra trong quá trình xử lý thủ tục Check-out: " + e.getMessage());
        }
        return "redirect:/admin/checkout-room";
    }

    protected String render(Model model, String viewPath) {
        model.addAttribute("view", viewPath);
        model.addAttribute("extra_css", viewPath + " :: extra_css");
        model.addAttribute("extra_js", viewPath + " :: extra_js");
        return "layout/layout";
    }
}