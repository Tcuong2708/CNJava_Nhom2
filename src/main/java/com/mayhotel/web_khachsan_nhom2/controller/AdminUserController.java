package com.mayhotel.web_khachsan_nhom2.controller;

import org.springframework.ui.Model;
import com.mayhotel.web_khachsan_nhom2.model.User;
import com.mayhotel.web_khachsan_nhom2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
        model.addAttribute("user", user);
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

    // 4. Trang chỉnh sửa người dùng (GET)
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            ra.addFlashAttribute("error", "Không tìm thấy người dùng để chỉnh sửa!");
            return "redirect:/admin/users";
        }

        setPageTitle(model, "Chỉnh sửa tài khoản: " + user.getTenDangNhap());
        model.addAttribute("user", user);

        return render(model, "view/Admin/User/edit");
    }

    // 5. Xử lý cập nhật thông tin người dùng (POST)
    @PostMapping("/edit")
    public String update(User user, RedirectAttributes ra) {
        try {

            User existingUser = userRepository.findById(user.getIdTaiKhoan()).orElse(null);

            if (existingUser == null) {
                ra.addFlashAttribute("error", "Không tìm thấy người dùng để cập nhật!");
                return "redirect:/admin/users";
            }

            existingUser.setHoTen(user.getHoTen());
            existingUser.setSoDienThoai(user.getSoDienThoai());
            existingUser.setDiaChi(user.getDiaChi());
            existingUser.setQuocTich(user.getQuocTich());
            existingUser.setRoleID(user.getRoleID());

            userRepository.save(existingUser);
            ra.addFlashAttribute("success", "Cập nhật thông tin tài khoản thành công!");

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Có lỗi xảy ra trong quá trình cập nhật: " + e.getMessage());
        }

        return "redirect:/admin/users";
    }

    // 6. Trang thêm mới người dùng (GET)
    @GetMapping("/create")
    public String create(Model model) {
        setPageTitle(model, "Thêm mới tài khoản");

        // Truyền một đối tượng User trống để Thymeleaf binding
        model.addAttribute("user", new User());

        return render(model, "view/Admin/User/create");
    }

    // 7. Xử lý lưu người dùng mới (POST)
    @PostMapping("/create")
    public String store(User user, RedirectAttributes ra) {
        try {
            // Kiểm tra xem tên đăng nhập đã tồn tại chưa
            // if (userRepository.existsByTenDangNhap(user.getTenDangNhap())) { ... }

            // Lưu người dùng mới
            userRepository.save(user);
            ra.addFlashAttribute("success", "Thêm mới tài khoản thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi khi tạo tài khoản: " + e.getMessage());
            return "redirect:/admin/users/create";
        }
        return "redirect:/admin/users";
    }

    // =========================================================================
    // KHÓA / MỞ KHÓA TÀI KHOẢN
    // =========================================================================
    @GetMapping("/toggle-status/{id}")
    public String toggleStatus(@PathVariable("id") Integer id, RedirectAttributes ra) {
        try {
            User user = userRepository.findById(id).orElse(null);

            if (user == null) {
                ra.addFlashAttribute("error", "Không tìm thấy người dùng!");
                return "redirect:/admin/users";
            }

            if (user.getTrangThai() == null || user.getTrangThai() == true) {
                user.setTrangThai(false); // Khóa tài khoản
                ra.addFlashAttribute("success", "Đã khóa tài khoản của " + user.getTenDangNhap() + " thành công!");
            } else {
                user.setTrangThai(true); // Mở khóa
                ra.addFlashAttribute("success", "Đã mở khóa hoạt động cho tài khoản " + user.getTenDangNhap() + "!");
            }

            userRepository.save(user);
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
}