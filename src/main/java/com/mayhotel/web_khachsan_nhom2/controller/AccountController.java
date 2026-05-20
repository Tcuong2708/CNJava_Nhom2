package com.mayhotel.web_khachsan_nhom2.controller;

import com.mayhotel.web_khachsan_nhom2.model.User;
import com.mayhotel.web_khachsan_nhom2.repository.LoaiPhongRepository;
import com.mayhotel.web_khachsan_nhom2.repository.PhongRepository;
import com.mayhotel.web_khachsan_nhom2.repository.UserRepository;
import com.mayhotel.web_khachsan_nhom2.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class AccountController extends BaseController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PhongRepository phongRepository;

    @Autowired
    private LoaiPhongRepository loaiPhongRepository;

    @Autowired
    private UserService userService;

    // =========================================================================
    // 1. ĐĂNG NHẬP / ĐĂNG XUẤT (TÍCH HỢP COOKIE REMEMBER ME)
    // =========================================================================

    @GetMapping("/account/login")
    public String login(
            @CookieValue(value = "remember_hotel_user", required = false) String rememberedUsername,
            HttpSession session,
            Model model) {

        if (rememberedUsername != null && session.getAttribute("user") == null) {
            User autoUser = userRepository.findByTenDangNhap(rememberedUsername);

            // Kiểm tra tài khoản tồn tại và không bị Admin khóa hoạt động
            if (autoUser != null && (autoUser.getTrangThai() == null || !autoUser.getTrangThai().equals(0))) {
                session.setAttribute("user", autoUser);
                session.setAttribute("cartCount", 0);
                System.out.println("-> [REMEMBER ME] Tự động tái lập phiên đăng nhập cho tài khoản: " + rememberedUsername);
                return "redirect:/"; // Điều hướng thẳng vào trang chủ
            }
        }

        setPageTitle(model, "Đăng nhập hệ thống");
        setExtraCSS(model, "view/Account/login :: extra_css");
        return render(model, "view/Account/login");
    }

    @PostMapping("/account/login")
    public String postLogin(
            @RequestParam("TenDangNhap") String username,
            @RequestParam("MatKhau") String password,
            @RequestParam(value = "rememberMe", required = false) Boolean rememberMe,
            HttpServletResponse response,
            HttpSession session,
            Model model) {

        User user = userRepository.findByTenDangNhap(username);

        if (user != null && user.getMatKhau().equals(password)) {

            if (user.getTrangThai() != null && (user.getTrangThai().equals(0) || user.getTrangThai().equals(false))) {
                model.addAttribute("error", "Tài khoản của bạn đã bị khóa! Vui lòng liên hệ bộ phận hỗ trợ.");
                setPageTitle(model, "Đăng nhập hệ thống");
                setExtraCSS(model, "view/Account/login :: extra_css");
                return render(model, "view/Account/login");
            }

            // XỬ LÝ LƯU PHIÊN ĐĂNG NHẬP (REMEMBER ME COOKIE)
            if (rememberMe != null && rememberMe) {
                Cookie cookie = new Cookie("remember_hotel_user", username);
                cookie.setMaxAge(7 * 24 * 60 * 60); // Thời gian sống: 7 ngày
                cookie.setPath("/");
                cookie.setHttpOnly(true);
                response.addCookie(cookie);
            } else {
                Cookie cookie = new Cookie("remember_hotel_user", null);
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);
            }

            session.setAttribute("user", user);
            session.setAttribute("cartCount", 0);
            return "redirect:/";
        } else {
            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không chính xác!");
            setPageTitle(model, "Đăng nhập hệ thống");
            setExtraCSS(model, "view/Account/login :: extra_css");
            return render(model, "view/Account/login");
        }
    }

    @GetMapping("/account/logout")
    public String logout(HttpSession session, HttpServletResponse response) {
        session.invalidate();

        Cookie cookie = new Cookie("remember_hotel_user", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        return "redirect:/account/login";
    }

    // =========================================================================
    // 2. ĐĂNG KÝ TÀI KHOẢN MỚI
    // =========================================================================

    @GetMapping("/account/register")
    public String register(Model model) {
        setPageTitle(model, "Đăng ký tài khoản");
        model.addAttribute("user", new User());
        return render(model, "view/Account/register");
    }

    @PostMapping("/account/register")
    public String postRegister(@ModelAttribute("user") User user, HttpSession session, Model model) {
        System.out.println("-> [ĐƯỜNG ĐI CỦA BIẾN] Email nhận từ Form HTML là: '" + user.getEmail() + "'");
        try {
            // -----------------------------------------------------------------
            // RÀNG BUỘC KIỂM TRA DỮ LIỆU ĐẦU VÀO (VALIDATION)
            // -----------------------------------------------------------------
            if (user.getMatKhau() == null || user.getMatKhau().trim().length() < 6) {
                model.addAttribute("error", "Đăng ký thất bại: Mật khẩu phải có độ dài tối thiểu từ 6 ký tự trở lên!");
                setPageTitle(model, "Đăng ký tài khoản");
                return render(model, "view/Account/register");
            }

            String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
            if (user.getEmail() == null || !user.getEmail().matches(emailRegex)) {
                model.addAttribute("error", "Đăng ký thất bại: Định dạng Email không hợp lệ!");
                setPageTitle(model, "Đăng ký tài khoản");
                return render(model, "view/Account/register");
            }

            String phoneRegex = "^\\+?\\d{10,}$";
            if (user.getSoDienThoai() != null && !user.getSoDienThoai().isEmpty()) {
                if (!user.getSoDienThoai().matches(phoneRegex)) {
                    model.addAttribute("error", "Đăng ký thất bại: Số điện thoại phải chứa ít nhất 10 số!");
                    setPageTitle(model, "Đăng ký tài khoản");
                    return render(model, "view/Account/register");
                }
            }

            User existingUser = userRepository.findByTenDangNhap(user.getTenDangNhap());
            if (existingUser != null) {
                model.addAttribute("error", "Tên đăng nhập này đã tồn tại trên hệ thống!");
                setPageTitle(model, "Đăng ký tài khoản");
                return render(model, "view/Account/register");
            }

            // -----------------------------------------------------------------
            // XỬ LÝ GỬI MÃ KÍCH HOẠT THẬT
            // -----------------------------------------------------------------
            String regOtp = userService.generateOTP();

            // Lưu trữ tạm thời trạng thái người dùng vào Session gác cổng
            session.setAttribute("pendingUser", user);
            session.setAttribute("registerOtpCode", regOtp);
            session.setAttribute("registerOtpExpiry", System.currentTimeMillis() + 300000); // Hiệu lực 5 phút

            // Thực thi bắn Email thật sang địa chỉ hòm thư khách hàng vừa nhập
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                try {
                    System.out.println("-> [MAY HOTEL DEBUG]: Bắt đầu gõ cửa dịch vụ UserService để gửi mail thật...");
                    userService.sendEmail(user.getEmail(), regOtp);
                    System.out.println("-> [MAY HOTEL DEBUG]: Lệnh gọi JavaMailSender đã thực thi xong không crash!");
                } catch (Exception e) {
                    System.err.println(" [XUẤT HIỆN LỖI SMTP CHÍ MẠNG] - CHI TIẾT LỖI:");
                    e.printStackTrace();
                }
            } else {
                System.out.println("-> [MAY HOTEL WARNING]: Biến email bị rỗng hoặc null, bỏ qua luồng gửi mail!");
            }

            return "redirect:/account/verify_register_otp";

        } catch (Exception e) {
            model.addAttribute("error", "Hệ thống gặp lỗi xử lý: " + e.getMessage());
            model.addAttribute("error", "Lỗi kết nối Gmail SMTP thật: " + e.getMessage());
            System.out.println("-> [SMTP ERROR] Thất bại khi gửi Mail đăng ký: " + e.getMessage());
            setPageTitle(model, "Đăng ký tài khoản");
            return render(model, "view/Account/register");
        }
    }

    @GetMapping("/account/verify_register_otp")
    public String verifyRegisterOtpPage(Model model, HttpSession session) {
        if (session.getAttribute("pendingUser") == null) {
            return "redirect:/account/register";
        }
        setPageTitle(model, "Xác thực kích hoạt tài khoản");
        setExtraCSS(model, "view/Account/verify_register_otp :: extra_css");
        return render(model, "view/Account/verify_register_otp");
    }

    @PostMapping("/account/verify_register_otp")
    public String processVerifyRegisterOtp(@RequestParam("otp") String userOtp, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String serverOtp = (String) session.getAttribute("registerOtpCode");
        Long expiryTime = (Long) session.getAttribute("registerOtpExpiry");
        User pendingUser = (User) session.getAttribute("pendingUser");

        if (pendingUser == null) {
            return "redirect:/account/register";
        }

        if (expiryTime != null && System.currentTimeMillis() > expiryTime) {
            model.addAttribute("error", "Mã OTP xác thực đã hết hạn! Vui lòng tiến hành đăng ký lại.");
            session.removeAttribute("pendingUser");
            session.removeAttribute("registerOtpCode");
            session.removeAttribute("registerOtpExpiry");
            setPageTitle(model, "Đăng ký tài khoản");
            return render(model, "view/Account/register");
        }

        if (serverOtp != null && serverOtp.equals(userOtp)) {
            pendingUser.setRoleID(3); // Mặc định gán quyền Khách hàng (Role ID = 3)
            userRepository.save(pendingUser);

            session.removeAttribute("pendingUser");
            session.removeAttribute("registerOtpCode");
            session.removeAttribute("registerOtpExpiry");

            redirectAttributes.addFlashAttribute("success", "Đăng ký tài khoản thành công! Vui lòng tiến hành đăng nhập.");
            return "redirect:/account/login";
        }

        model.addAttribute("error", "Mã xác thực OTP không chính xác!");
        setPageTitle(model, "Xác thực kích hoạt tài khoản");
        setExtraCSS(model, "view/Account/verify_register_otp :: extra_css");
        return render(model, "view/Account/verify_register_otp");
    }

    // =========================================================================
    // 3. KHÔI PHỤC MẬT KHẨU
    // =========================================================================

    @GetMapping("/account/forgot_password")
    public String forgotPassword(Model model) {
        setPageTitle(model, "Tìm tài khoản");
        setExtraCSS(model, "view/Account/verify_otp :: extra_css");
        return render(model, "view/Account/find_account");
    }

    @PostMapping("/account/find_account")
    public String processFindAccount(
            @RequestParam(value = "keyword", required = false) String keyword,
            HttpSession session,
            Model model) {

        if (keyword == null || keyword.isEmpty()) {
            keyword = (String) session.getAttribute("resetIdentifier");
        }

        if (keyword == null || keyword.isEmpty()) {
            return "redirect:/account/forgot_password";
        }

        User user = userRepository.findByEmail(keyword);
        if (user == null) {
            user = userRepository.findBySoDienThoai(keyword);
        }

        if (user != null) {
            String otp = userService.generateOTP();

            session.setAttribute("otpCode", otp);
            session.setAttribute("otpExpiryTime", System.currentTimeMillis() + 300000);
            session.setAttribute("otpAttempts", 0);
            session.setAttribute("resetIdentifier", keyword);

            // Bắn mã OTP bảo mật thực tế trực tiếp vào Email khôi phục của khách
            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                try {
                    userService.sendEmail(user.getEmail(), otp);
                } catch (Exception e) {
                    System.out.println("-> [SMTP ERROR] Thất bại khi gửi Mail khôi phục mật khẩu: " + e.getMessage());
                }
            }

            return "redirect:/account/verify_otp";
        }

        model.addAttribute("error", "Không tìm thấy tài khoản trên hệ thống, vui lòng kiểm tra lại!");
        setPageTitle(model, "Tìm tài khoản");
        setExtraCSS(model, "view/Account/find_account :: extra_css");
        return render(model, "view/Account/find_account");
    }

    @GetMapping("/account/verify_otp")
    public String verifyOtpPage(Model model, HttpSession session) {
        if (session.getAttribute("resetIdentifier") == null) {
            return "redirect:/account/forgot_password";
        }
        setPageTitle(model, "Xác thực OTP");
        setExtraCSS(model, "view/Account/verify_otp :: extra_css");
        return render(model, "view/Account/verify_otp");
    }

    @PostMapping("/account/verify_otp")
    public String processResetPassword(
            @RequestParam("otp") String otp,
            @RequestParam("newpassword") String newPassword,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        String serverOtp = (String) session.getAttribute("otpCode");
        String identifier = (String) session.getAttribute("resetIdentifier");

        if (newPassword == null || newPassword.length() < 6) {
            model.addAttribute("error", "Mật khẩu mới phải có ít nhất 6 ký tự!");
            setPageTitle(model, "Xác thực OTP");
            setExtraCSS(model, "view/Account/verify_otp :: extra_css");
            return render(model, "view/Account/verify_otp");
        }

        Integer otpAttempts = (Integer) session.getAttribute("otpAttempts");
        if (otpAttempts == null) otpAttempts = 0;

        if (otpAttempts >= 5) {
            model.addAttribute("error", "Bạn đã nhập sai quá 5 lần quy định. Vui lòng yêu cầu mã OTP mới!");
            setPageTitle(model, "Xác thực OTP");
            setExtraCSS(model, "view/Account/verify_otp :: extra_css");
            return render(model, "view/Account/verify_otp");
        }

        Long otpExpiryTime = (Long) session.getAttribute("otpExpiryTime");
        if (otpExpiryTime != null && System.currentTimeMillis() > otpExpiryTime) {
            model.addAttribute("error", "Mã OTP đã hết hạn hiệu lực bảo mật. Vui lòng yêu cầu lại mã mới!");
            session.removeAttribute("otpCode");
            session.removeAttribute("otpExpiryTime");
            setPageTitle(model, "Xác thực OTP");
            setExtraCSS(model, "view/Account/verify_otp :: extra_css");
            return render(model, "view/Account/verify_otp");
        }

        if (serverOtp != null && serverOtp.equals(otp)) {
            userService.updatePassword(identifier, newPassword);

            session.removeAttribute("otpCode");
            session.removeAttribute("resetIdentifier");
            session.removeAttribute("otpAttempts");
            session.removeAttribute("otpExpiryTime");

            redirectAttributes.addFlashAttribute("success", "Đặt lại mật khẩu thành công! Vui lòng đăng nhập.");
            return "redirect:/account/login";
        }

        otpAttempts++;
        session.setAttribute("otpAttempts", otpAttempts);

        int remainingAttempts = 5 - otpAttempts;
        String errorMsg = "Mã OTP không chính xác! Còn lại " + remainingAttempts + " lần thử.";
        if (remainingAttempts <= 1) {
            errorMsg = "Mã OTP không chính xác! Đây là lần thử cuối cùng của bạn!";
        }

        model.addAttribute("error", errorMsg);
        setPageTitle(model, "Xác thực OTP");
        setExtraCSS(model, "view/Account/verify_otp :: extra_css");
        return render(model, "view/Account/verify_otp");
    }

    // =========================================================================
    // 4. QUẢN LÝ HỒ SƠ CÁ NHÂN (PROFILE)
    // =========================================================================

    @GetMapping("/account/profile")
    public String showProfile(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/account/login";
        }

        model.addAttribute("user", currentUser);
        setPageTitle(model, "Hồ sơ của tôi");
        setExtraCSS(model, "view/Account/profile :: extra_css");
        return render(model, "view/Account/profile");
    }

    @PostMapping("/account/update-profile")
    public String updateProfile(@ModelAttribute User updatedData, HttpSession session, RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/account/login";
        }

        try {
            currentUser.setHoTen(updatedData.getHoTen());
            currentUser.setSoDienThoai(updatedData.getSoDienThoai());
            currentUser.setQuocTich(updatedData.getQuocTich());
            currentUser.setDiaChi(updatedData.getDiaChi());

            userRepository.save(currentUser);
            session.setAttribute("user", currentUser);

            redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin hồ sơ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra trong quá trình lưu dữ liệu hồ sơ!");
        }

        return "redirect:/account/profile";
    }
}