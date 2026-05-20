package com.mayhotel.web_khachsan_nhom2.controller;

import com.mayhotel.web_khachsan_nhom2.model.PhieuDat;
import com.mayhotel.web_khachsan_nhom2.model.Phong;
import com.mayhotel.web_khachsan_nhom2.model.User;
import com.mayhotel.web_khachsan_nhom2.repository.PhieuDatRepository;
import com.mayhotel.web_khachsan_nhom2.repository.PhongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/invoices") // URL đích: http://localhost:8080/admin/invoices
public class AdminInvoiceController extends BaseController {

    @Autowired
    private PhieuDatRepository phieuDatRepository;

    @Autowired
    private PhongRepository phongRepository;

    // 1. TRANG DANH SÁCH ĐƠN ĐẶT PHÒNG (Index)
    @GetMapping("")
    public String index(HttpSession session, Model model) {
        setPageTitle(model, "Quản lý Đơn đặt phòng");

        // Kiểm tra quyền Admin hiển thị nút xóa
        User currentUser = (User) session.getAttribute("user");
        boolean isAdmin = currentUser != null && currentUser.getRoleID() == 1;
        model.addAttribute("isAdmin", isAdmin);

        List<PhieuDat> list = phieuDatRepository.findAll();
        model.addAttribute("invoices", list);

        setExtraCSS(model, "view/Admin/Invoice/index :: extra_css");
        return render(model, "view/Admin/Invoice/index");
    }

    // 2. XEM CHI TIẾT ĐƠN ĐẶT PHÒNG
    @GetMapping("/details/{id}")
    public String details(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<PhieuDat> phieuOpt = phieuDatRepository.findById(id);
        if (phieuOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn đặt phòng!");
            return "redirect:/admin/invoices";
        }

        setPageTitle(model, "Chi tiết đơn đặt phòng");
        model.addAttribute("invoice", phieuOpt.get());

        setExtraCSS(model, "view/Admin/Invoice/details :: extra_css");
        return render(model, "view/Admin/Invoice/details");
    }

    // 3. TẠO ĐƠN KHÁCH LẺ (GET)
    @GetMapping("/create")
    public String create(Model model) {
        setPageTitle(model, "Đặt phòng khách lẻ");

        // Lọc danh sách phòng trống thực tế (MaTrangThai == 1) đổ vào dropdown
        List<Phong> phongTrong = phongRepository.findAll().stream()
                .filter(p -> p.getMaTrangThai() != null && p.getMaTrangThai() == 1)
                .collect(Collectors.toList());

        model.addAttribute("phongTrong", phongTrong);
        model.addAttribute("invoice", new PhieuDat());

        return render(model, "view/Admin/Invoice/create");
    }

    // 4. XỬ LÝ TẠO ĐƠN KHÁCH LẺ (POST)
    @PostMapping("/create")
    public String postCreate(@ModelAttribute("invoice") PhieuDat invoice,
                             @RequestParam("maPhong") Integer maPhong,
                             RedirectAttributes redirectAttributes) {
        try {
            Optional<Phong> phongOpt = phongRepository.findById(maPhong);
            if (phongOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Phòng được chọn không hợp lệ!");
                return "redirect:/admin/invoices/create";
            }

            Phong phong = phongOpt.get();

            // Tính số đêm ở từ Ngày nhận và Ngày trả
            long soDem = ChronoUnit.DAYS.between(invoice.getNgayCheckIn(), invoice.getNgayCheckOut());
            if (soDem < 1) soDem = 1;

            // Tính tổng tiền = Đơn giá phòng * Số đêm
            BigDecimal giaPhong = phong.getPrice();
            BigDecimal tongTien = giaPhong.multiply(new BigDecimal(soDem));

            // Thiết lập các thuộc tính mặc định
            invoice.setMaPhong(phong.getId());
            invoice.setNgayDat(LocalDateTime.now());
            invoice.setTotalPrice(tongTien);
            invoice.setPhuThu(BigDecimal.ZERO);
            invoice.setDaThanhToan(true); // Khách vãng lai thanh toán trực tiếp tại quầy

            // Lưu xuống CSDL
            phieuDatRepository.save(invoice);

            // Chuyển trạng thái phòng sang Đang ở (Mã số 2)
            phong.setMaTrangThai(2);
            phongRepository.save(phong);

            redirectAttributes.addFlashAttribute("success", "Tạo đơn đặt phòng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi nhập liệu thời gian lưu trú không hợp lệ!");
            return "redirect:/admin/invoices/create";
        }
        return "redirect:/admin/invoices";
    }

    // 5. TRANG XÁC NHẬN XÓA ĐƠN (GET)
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null || currentUser.getRoleID() != 1) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền thực hiện chức năng này!");
            return "redirect:/admin/invoices";
        }

        Optional<PhieuDat> phieuOpt = phieuDatRepository.findById(id);
        if (phieuOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn đặt cần xóa!");
            return "redirect:/admin/invoices";
        }

        setPageTitle(model, "Xác nhận xóa đơn đặt");
        model.addAttribute("invoice", phieuOpt.get());

        setExtraCSS(model, "view/Admin/Invoice/delete :: extra_css");
        return render(model, "view/Admin/Invoice/delete");
    }

    // 6. XỬ LÝ XÓA VĨNH VIỄN (POST)
    @PostMapping("/delete/{id}")
    public String postDelete(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            Optional<PhieuDat> phieuOpt = phieuDatRepository.findById(id);
            if (phieuOpt.isPresent()) {
                PhieuDat phieu = phieuOpt.get();
                Optional<Phong> phongOpt = phongRepository.findById(phieu.getMaPhong());
                if (phongOpt.isPresent()) {
                    Phong phong = phongOpt.get();
                    phong.setMaTrangThai(1);
                    phongRepository.save(phong);
                }
                phieuDatRepository.deleteById(id);
                redirectAttributes.addFlashAttribute("success", "Xóa đơn đặt phòng thành công vĩnh viễn!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi thực hiện xóa!");
        }
        return "redirect:/admin/invoices";
    }

    // 7. IN HÓA ĐƠN ĐỘC LẬP
    @GetMapping("/print/{id}")
    public String printInvoice(@PathVariable("id") Integer id, Model model) {
        Optional<PhieuDat> phieuOpt = phieuDatRepository.findById(id);
        if (phieuOpt.isEmpty()) {
            return "redirect:/error/404";
        }

        PhieuDat phieu = phieuOpt.get();
        long soDem = ChronoUnit.DAYS.between(phieu.getNgayCheckIn(), phieu.getNgayCheckOut());
        if (soDem < 1) soDem = 1;

        model.addAttribute("invoice", phieu);
        model.addAttribute("soDem", soDem);

        return "view/Admin/Invoice/print";
    }
}