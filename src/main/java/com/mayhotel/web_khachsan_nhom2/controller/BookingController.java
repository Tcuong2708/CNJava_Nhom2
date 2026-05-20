package com.mayhotel.web_khachsan_nhom2.controller;

import com.mayhotel.web_khachsan_nhom2.model.*;
import com.mayhotel.web_khachsan_nhom2.repository.PhongRepository;
import com.mayhotel.web_khachsan_nhom2.repository.LoaiPhongRepository;
import com.mayhotel.web_khachsan_nhom2.repository.PhieuDatRepository;
import com.mayhotel.web_khachsan_nhom2.repository.DichVuRepository; // Bổ sung import Repo dịch vụ
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/booking")
public class BookingController extends BaseController {

    @Autowired
    private PhongRepository phongRepository;

    @Autowired
    private LoaiPhongRepository loaiPhongRepository;

    @Autowired
    private PhieuDatRepository phieuDatRepository;

    @Autowired
    private DichVuRepository dichVuRepository;

    private GioHang getGioHang(HttpSession session) {
        GioHang cart = (GioHang) session.getAttribute("Cart");
        if (cart == null) {
            cart = new GioHang();
            session.setAttribute("Cart", cart);
        }
        return cart;
    }

    // ==========================================
    // 1. HIỂN THỊ GIỎ HÀNG (DANH SÁCH PHÒNG CHỜ ĐẶT)
    // ==========================================
    @GetMapping("")
    public String index(HttpSession session, Model model) {
        setPageTitle(model, "Phòng đã chọn đặt");
        GioHang cart = getGioHang(session);

        model.addAttribute("cart", cart);
        model.addAttribute("listHoaDon", cart.getItems());

        // Đẩy danh sách dịch vụ đi kèm ra view giỏ hàng
        model.addAttribute("listDichVu", dichVuRepository.findAll());

        // 🌟 KHẮC PHỤC: Đã dọn sạch đoạn mã trùng lặp thừa ở cuối hàm cũ
        setExtraCSS(model, "view/Booking/cart :: extra_css");
        return render(model, "view/Booking/cart");
    }

    // =========================================================================
    // 2. TIẾP NHẬN DỊCH VỤ KHÁCH CHỌN VÀ ĐẨY SANG CHECKOUT
    // =========================================================================
    @PostMapping("/select-services")
    public String selectServices(
            @RequestParam(value = "selectedServiceIds", required = false) List<Integer> serviceIds,
            HttpSession session) {

        if (serviceIds == null) {
            serviceIds = new ArrayList<>();
        }
        session.setAttribute("selectedServiceIds", serviceIds);
        return "redirect:/booking/checkout";
    }

    // ==========================================
    // 3. TRANG CHI TIẾT ĐẶT PHÒNG BAN ĐẦU
    // ==========================================
    @GetMapping("/book")
    public String book(@RequestParam("maLoai") Integer maLoai, Model model, RedirectAttributes redirectAttributes) {
        Optional<LoaiPhong> loaiOpt = loaiPhongRepository.findById(maLoai);
        if (loaiOpt.isEmpty()) {
            return "redirect:/error/404";
        }

        LoaiPhong loai = loaiOpt.get();
        long phongTrong = phongRepository.findAll().stream()
                .filter(p -> p.getMaLoai().equals(maLoai) && p.getMaTrangThai() != null && p.getMaTrangThai() == 1)
                .count();

        if (phongTrong == 0) {
            redirectAttributes.addFlashAttribute("error", "Rất tiếc, loại phòng này hiện tại đã hết phòng trống!");
            return "redirect:/";
        }

        setPageTitle(model, "Đặt phòng: " + loai.getName());
        model.addAttribute("loaiPhong", loai);
        model.addAttribute("soPhongTrong", phongTrong);

        setExtraCSS(model, "view/Booking/booking :: extra_css");
        return render(model, "view/Booking/booking");
    }

    // ==========================================
    // 4. THÊM PHÒNG VÀO GIỎ HÀNG TEMPLATE
    // ==========================================
    @PostMapping("/add-to-cart")
    public String addToCart(@RequestParam("maLoai") Integer maLoai,
                            @RequestParam("soLuong") int soLuong,
                            @RequestParam("ngayNhan") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayNhan,
                            @RequestParam("ngayTra") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayTra,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {

        if (!ngayTra.isAfter(ngayNhan)) {
            redirectAttributes.addFlashAttribute("error", "Ngày trả phòng phải sau ngày nhận phòng!");
            return "redirect:/booking/book?maLoai=" + maLoai;
        }

        Optional<LoaiPhong> loaiOpt = loaiPhongRepository.findById(maLoai);
        long phongTrong = phongRepository.findAll().stream()
                .filter(p -> p.getMaLoai().equals(maLoai) && p.getMaTrangThai() != null && p.getMaTrangThai() == 1)
                .count();

        if (soLuong > phongTrong) {
            redirectAttributes.addFlashAttribute("error", "Chỉ còn " + phongTrong + " phòng trống. Vui lòng chọn lại số lượng.");
            return "redirect:/booking/book?maLoai=" + maLoai;
        }

        if (loaiOpt.isPresent()) {
            CartItem item = new CartItem();
            item.setLoaiPhong(loaiOpt.get());
            item.setSoLuong(soLuong);
            item.setNgayNhan(ngayNhan);
            item.setNgayTra(ngayTra);

            GioHang cart = getGioHang(session);
            cart.add(item);
            session.setAttribute("Cart", cart);
            session.setAttribute("cartCount", cart.getItems().size());

            redirectAttributes.addFlashAttribute("success", "Đã thêm phòng vào danh sách chọn đặt thành công!");
        }
        return "redirect:/booking";
    }

    // ==========================================
    // 5. CẬP NHẬT THÔNG TIN TRONG GIỎ HÀNG
    // ==========================================
    @PostMapping("/update-cart")
    public String updateCart(@RequestParam("maLoai") Integer maLoai,
                             @RequestParam("soLuong") int soLuong,
                             @RequestParam("ngayNhan") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayNhan,
                             @RequestParam("ngayTra") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayTra,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {

        if (!ngayTra.isAfter(ngayNhan)) {
            redirectAttributes.addFlashAttribute("error", "Ngày trả phải lớn hơn ngày nhận phòng!");
            return "redirect:/booking";
        }

        GioHang cart = getGioHang(session);
        CartItem item = cart.getItems().stream()
                .filter(x -> x.getLoaiPhong().getMaLoai().equals(maLoai))
                .findFirst().orElse(null);

        if (item != null) {
            long thucTeTrong = phongRepository.findAll().stream()
                    .filter(p -> p.getMaLoai().equals(maLoai) && p.getMaTrangThai() != null && p.getMaTrangThai() == 1)
                    .count();

            if (soLuong > thucTeTrong) {
                redirectAttributes.addFlashAttribute("error", "Chỉ còn " + thucTeTrong + " phòng trống cho loại này.");
                item.setSoLuong((int) thucTeTrong);
            } else {
                item.setSoLuong(soLuong);
            }

            item.setNgayNhan(ngayNhan);
            item.setNgayTra(ngayTra);
            session.setAttribute("Cart", cart);
            redirectAttributes.addFlashAttribute("success", "Đã cập nhật thay đổi thành công!");
        }
        return "redirect:/booking";
    }

    // ==========================================
    // 6. XÓA BỎ PHÒNG KHỎI GIỎ HÀNG
    // ==========================================
    @GetMapping("/remove")
    public String remove(@RequestParam("id") int id, HttpSession session, RedirectAttributes redirectAttributes) {
        GioHang cart = getGioHang(session);
        cart.remove(id);

        session.setAttribute("Cart", cart);
        session.setAttribute("cartCount", cart.getItems().size());

        redirectAttributes.addFlashAttribute("success", "Đã hủy chọn phòng nghỉ thành công.");
        return "redirect:/booking";
    }

    // ==========================================
    // 7. TRANG XÁC NHẬN CHỌN PHƯƠNG THỨC THANH TOÁN (GET)
    // ==========================================
    @GetMapping("/checkout")
    public String checkout(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập trước khi thanh toán!");
            return "redirect:/account/login";
        }

        GioHang cart = (GioHang) session.getAttribute("Cart");
        if (cart == null || cart.getItems().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Danh sách phòng đặt của bạn đang trống!");
            return "redirect:/booking";
        }

        // XỬ LÝ ĐỔ DỮ LIỆU DỊCH VỤ ĐÃ CHỌN TỪ SESSION RA VIEW
        List<Integer> serviceIds = (List<Integer>) session.getAttribute("selectedServiceIds");
        java.math.BigDecimal totalDichVu = java.math.BigDecimal.ZERO;
        List<com.mayhotel.web_khachsan_nhom2.model.DichVu> chosenServices = new ArrayList<>();

        if (serviceIds != null && !serviceIds.isEmpty()) {
            chosenServices = dichVuRepository.findAllById(serviceIds);
            for (com.mayhotel.web_khachsan_nhom2.model.DichVu dv : chosenServices) {
                totalDichVu = totalDichVu.add(dv.getGiaTien());
            }
        }

        setPageTitle(model, "Xác nhận thanh toán đơn đặt");
        model.addAttribute("cart", cart);

        model.addAttribute("chosenServices", chosenServices);
        model.addAttribute("totalDichVu", totalDichVu);
        model.addAttribute("finalTotal", cart.getTongTienGioHang().add(totalDichVu));

        setExtraCSS(model, "view/Booking/checkout :: extra_css");
        return render(model, "view/Booking/checkout");
    }

    // =========================================================================
    // 8. PHÂN LUỒNG XỬ LÝ THANH TOÁN DATABASE & ĐẨY SANG CỔNG GIẢ LẬP (POST)
    // =========================================================================
    @PostMapping("/process-checkout")
    public String processCheckout(
            @RequestParam(value = "phuongThucThanhToan", required = false) String phuongThucThanhToan,
            @RequestParam(value = "ghiChu", required = false) String ghiChu,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) return "redirect:/account/login";

        GioHang cart = (GioHang) session.getAttribute("Cart");
        if (cart == null || cart.getItems().isEmpty()) {
            model.addAttribute("error", "Danh sách phòng đặt chờ thanh toán của bạn đang trống!");
            setPageTitle(model, "Xác nhận thanh toán đơn đặt");
            model.addAttribute("cart", new GioHang());
            return render(model, "view/Booking/checkout");
        }

        // 🌟 NÂNG CẤP: Đọc thông tin dịch vụ đi kèm khách đã tích chọn từ Session
        List<Integer> serviceIds = (List<Integer>) session.getAttribute("selectedServiceIds");
        java.math.BigDecimal totalDichVu = java.math.BigDecimal.ZERO;
        StringBuilder chuoiDichVu = new StringBuilder();

        if (serviceIds != null && !serviceIds.isEmpty()) {
            List<DichVu> chosenServices = dichVuRepository.findAllById(serviceIds);
            for (DichVu dv : chosenServices) {
                totalDichVu = totalDichVu.add(dv.getGiaTien());
                if (chuoiDichVu.length() > 0) chuoiDichVu.append(", ");
                chuoiDichVu.append(dv.getTenDV());
            }
        }

        Integer generatedMaHD = null;
        boolean isFirstInvoice = true;

        try {
            for (CartItem item : cart.getItems()) {
                Integer maLoai = item.getLoaiPhong().getMaLoai();
                int soLuongDat = item.getSoLuong();

                List<Phong> danhSachPhongTrong = phongRepository.findAll().stream()
                        .filter(p -> p.getMaLoai().equals(maLoai) && p.getMaTrangThai() != null && p.getMaTrangThai() == 1)
                        .collect(Collectors.toList());

                if (danhSachPhongTrong.size() < soLuongDat) {
                    model.addAttribute("error", "Rất tiếc! Loại phòng " + item.getLoaiPhong().getName() + " vừa hết phòng trống.");
                    setPageTitle(model, "Xác nhận thanh toán đơn đặt");
                    model.addAttribute("cart", cart);
                    return render(model, "view/Booking/checkout");
                }

                for (int i = 0; i < soLuongDat; i++) {
                    Phong phongDuocChon = danhSachPhongTrong.get(i);

                    PhieuDat phieu = new PhieuDat();
                    phieu.setHoTen(currentUser.getHoTen());
                    phieu.setSdt(currentUser.getSoDienThoai());
                    phieu.setIdTaiKhoan(currentUser.getIdTaiKhoan());
                    phieu.setMaPhong(phongDuocChon.getId());
                    phieu.setNgayCheckIn(item.getNgayNhan());
                    phieu.setNgayCheckOut(item.getNgayTra());
                    phieu.setNgayDat(java.time.LocalDateTime.now());
                    phieu.setPhuThu(java.math.BigDecimal.ZERO);
                    phieu.setPhuongThucThanhToan(phuongThucThanhToan);
                    phieu.setDaThanhToan(false);

                    java.math.BigDecimal giaPhongCoBan = item.getLoaiPhong().getPrice().multiply(new java.math.BigDecimal(item.getSoDem()));

                    String ghiChuSach = (ghiChu != null) ? ghiChu.trim() : "";
                    if (chuoiDichVu.length() > 0) {
                        if (ghiChuSach.isEmpty() || ghiChuSach.equalsIgnoreCase("không")) {
                            phieu.setGhiChu("[DỊCH VỤ ĐI KÈM]: " + chuoiDichVu.toString());
                        } else {
                            phieu.setGhiChu(ghiChuSach + " | [DỊCH VỤ ĐI KÈM]: " + chuoiDichVu.toString());
                        }

                        if (isFirstInvoice) {
                            phieu.setTotalPrice(giaPhongCoBan.add(totalDichVu));
                            isFirstInvoice = false;
                        } else {
                            phieu.setTotalPrice(giaPhongCoBan);
                        }
                    } else {
                        phieu.setGhiChu(ghiChuSach);
                        phieu.setTotalPrice(giaPhongCoBan);
                    }

                    PhieuDat savedPhieu = phieuDatRepository.saveAndFlush(phieu);
                    generatedMaHD = savedPhieu.getId();

                    phongDuocChon.setMaTrangThai(2);
                    phongRepository.saveAndFlush(phongDuocChon);
                }
            }

            session.removeAttribute("Cart");
            session.removeAttribute("selectedServiceIds");
            session.setAttribute("cartCount", 0);

            if ("ONLINE".equalsIgnoreCase(phuongThucThanhToan)) {
                return "redirect:/booking/payment-gate?id=" + generatedMaHD;
            } else {
                redirectAttributes.addFlashAttribute("success", "Đặt phòng thành công! Quý khách vui lòng thanh toán trực tiếp tại quầy lễ tân.");
                return "redirect:/booking/success";
            }

        } catch (Exception e) {
            model.addAttribute("error", "Lỗi xử lý tạo hóa đơn đặt phòng: " + e.getMessage());
            setPageTitle(model, "Xác nhận thanh toán đơn đặt");
            model.addAttribute("cart", cart);
            return render(model, "view/Booking/checkout");
        }
    }

    // ==========================================
    // 9. HIỂN THỊ TRANG CỔNG THANH TOÁN GIẢ LẬP (GET)
    // ==========================================
    @GetMapping("/payment-gate")
    public String showPaymentGate(@RequestParam("id") Integer id, HttpSession session, Model model, RedirectAttributes ra) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) return "redirect:/account/login";

        Optional<PhieuDat> phieuOpt = phieuDatRepository.findById(id);
        if (phieuOpt.isEmpty()) {
            ra.addFlashAttribute("error", "Không tìm thấy thông tin hóa đơn cần kết nối thanh toán.");
            return "redirect:/";
        }

        setPageTitle(model, "Cổng thanh toán trực tuyến - MAY HOTEL");
        model.addAttribute("invoice", phieuOpt.get());
        return render(model, "view/Booking/payment_gate");
    }

    // ==========================================
    // 10. XÁC NHẬN HOÀN TẤT GIAO DỊCH QUA CỔNG GIẢ LẬP (POST)
    // ==========================================
    @PostMapping("/complete-online-payment")
    public String completeOnlinePayment(@RequestParam("maHD") Integer maHD, RedirectAttributes ra) {
        try {
            Optional<PhieuDat> phieuOpt = phieuDatRepository.findById(maHD);
            if (phieuOpt.isPresent()) {
                PhieuDat phieu = phieuOpt.get();
                phieu.setDaThanhToan(true);
                phieu.setGhiChu(phieu.getGhiChu() + " | [ONLINE] Đã quét mã thanh toán trực tuyến thành công.");
                phieuDatRepository.saveAndFlush(phieu);

                ra.addFlashAttribute("success", "Thanh toán trực tuyến thành công! Phòng của bạn đã được đảm bảo trên hệ thống.");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi kết toán giao dịch trực tuyến: " + e.getMessage());
        }
        return "redirect:/booking/success";
    }

    // ==========================================
    // 11. TRANG THÔNG BÁO ĐẶT PHÒNG THÀNH CÔNG
    // ==========================================
    @GetMapping("/success")
    public String success(Model model) {
        setPageTitle(model, "Đặt phòng thành công");
        setExtraCSS(model, "view/Booking/success :: extra_css");
        return render(model, "view/Booking/success");
    }

    // ==========================================
    // 12. TRANG LỊCH SỬ ĐẶT PHÒNG CỦA THÀNH VIÊN
    // ==========================================
    @GetMapping("/history")
    public String bookingHistory(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập để xem lịch sử lưu trú!");
            return "redirect:/account/login";
        }

        setPageTitle(model, "Lịch sử đặt phòng");
        List<PhieuDat> historyList = phieuDatRepository.findByIdTaiKhoan(currentUser.getIdTaiKhoan());
        model.addAttribute("historyList", historyList);

        setExtraCSS(model, "view/Booking/history :: extra_css");
        return render(model, "view/Booking/history");
    }

    // ==========================================
    // 13. CHI TIẾT ĐƠN ĐẶT PHÒNG KHÁCH HÀNG
    // ==========================================
    @GetMapping("/history/detail")
    public String bookingHistoryDetail(@RequestParam("id") Integer id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập hệ thống!");
            return "redirect:/account/login";
        }

        Optional<PhieuDat> phieuOpt = phieuDatRepository.findById(id);
        if (phieuOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy hóa đơn mã số này!");
            return "redirect:/booking/history";
        }

        PhieuDat phieu = phieuOpt.get();
        if (!phieu.getIdTaiKhoan().equals(currentUser.getIdTaiKhoan())) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền truy cập thông tin bảo mật này!");
            return "redirect:/booking/history";
        }

        setPageTitle(model, "Chi tiết đơn đặt phòng #" + phieu.getId());
        model.addAttribute("hoaDon", phieu);

        setExtraCSS(model, "view/Booking/historydetail :: extra_css");
        return render(model, "view/Booking/historydetail");
    }
}