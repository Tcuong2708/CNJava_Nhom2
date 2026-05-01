package com.mayhotel.web_khachsan_nhom2.controller;

import com.mayhotel.web_khachsan_nhom2.model.User;
import com.mayhotel.web_khachsan_nhom2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;

@Controller
public class AccountController extends BaseController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/account/login")
    public String login(Model model) {
        setPageTitle(model, "Đăng nhập hệ thống");
        setExtraCSS(model, "view/Account/login :: extra_css");
        return render(model, "view/Account/login");
    }

    @PostMapping("/account/login")
    public String postLogin(
            @RequestParam("TenDangNhap") String username,
            @RequestParam("MatKhau") String password,
            HttpSession session,
            Model model) {

        User user = userRepository.findByTenDangNhap(username);

        if (user != null && user.getMatKhau().equals(password)) {
            session.setAttribute("user", user);
            session.setAttribute("cartCount", 0);
            return "redirect:/";
        } else {
            model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không chính xác!");
            setPageTitle(model, "Đăng nhập hệ thống");
            return render(model, "view/Account/login");
        }
    }

    @GetMapping("/account/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/account/login";
    }
}