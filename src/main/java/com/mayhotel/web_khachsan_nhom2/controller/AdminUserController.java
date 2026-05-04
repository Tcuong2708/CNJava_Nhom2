package com.mayhotel.web_khachsan_nhom2.controller;

import org.springframework.ui.Model;
import com.mayhotel.web_khachsan_nhom2.model.User;
import com.mayhotel.web_khachsan_nhom2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController extends BaseController {

    @Autowired
    private UserRepository userRepository;

    // 1. Trang danh sách người dùng
    @GetMapping("")
    public String index(Model model) {
        setPageTitle(model, "Quản lý người dùng");
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);

        // Truyền trực tiếp đường dẫn đầy đủ
        return render(model, "view/Admin/User/index");
    }

    protected String render(Model model, String viewPath) {
        model.addAttribute("view", viewPath);
        model.addAttribute("extra_css", viewPath + " :: extra_css");
        model.addAttribute("extra_js", viewPath + " :: extra_js");
        return "layout/layout";
    }

    // 2. Xem chi tiết người dùng
    @GetMapping("/details/{id}")
    public String details(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            ra.addFlashAttribute("error", "Không tìm thấy người dùng!");
            return "redirect:/admin/users";
        }

        setPageTitle(model, "Chi tiết tài khoản: " + user.getTenDangNhap());
        model.addAttribute("u", user);
        return render(model, "view/Admin/User/details");
    }

    // 3. Xóa người dùng
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id, RedirectAttributes ra) {
        try {
            if (userRepository.existsById(id)) {
                userRepository.deleteById(id);
                ra.addFlashAttribute("success", "Đã xóa tài khoản thành công!");
            } else {
                ra.addFlashAttribute("error", "Tài khoản không tồn tại.");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Không thể xóa tài khoản này (có thể đã có dữ liệu liên quan như hóa đơn).");
        }
        return "redirect:/admin/users";
    }
}