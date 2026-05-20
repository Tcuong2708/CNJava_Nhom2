package com.mayhotel.web_khachsan_nhom2.controller;

import com.mayhotel.web_khachsan_nhom2.model.Phong;
import com.mayhotel.web_khachsan_nhom2.model.LoaiPhong;
import com.mayhotel.web_khachsan_nhom2.repository.PhongRepository;
import com.mayhotel.web_khachsan_nhom2.repository.LoaiPhongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/rooms")
public class AdminRoomController extends BaseController {

    @Autowired
    private PhongRepository phongRepository;

    @Autowired
    private LoaiPhongRepository loaiPhongRepository;

    // Hiển thị danh sách phòng
    @GetMapping("")
    public String index(Model model) {
        setPageTitle(model, "Quản lý phòng nghỉ");
        List<Phong> dsPhong = phongRepository.findAll();
        model.addAttribute("rooms", dsPhong);
        return render(model, "view/Admin/Room/index");
    }

    // PHƯƠNG THỨC XEM CHI TIẾT
    @GetMapping("/details/{id}")
    public String details(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        Phong phong = phongRepository.findById(id).orElse(null);

        if (phong == null) {
            ra.addFlashAttribute("error", "Không tìm thấy phòng này trong hệ thống!");
            return "redirect:/admin/rooms";
        }

        setPageTitle(model, "Chi tiết phòng: " + phong.getName());
        model.addAttribute("phong", phong);

        return render(model, "view/Admin/Room/details");
    }

    // Hiển thị form thêm mới
    @GetMapping("/create")
    public String create(Model model) {
        setPageTitle(model, "Thêm phòng mới");
        model.addAttribute("phong", new Phong());
        model.addAttribute("listLoai", loaiPhongRepository.findAll());
        return render(model, "view/Admin/Room/create");
    }

    // XỬ LÝ LƯU PHÒNG MỚI (ĐÃ BỔ SUNG RÀNG BUỘC)
    @PostMapping("/create")
    public String store(@ModelAttribute("phong") Phong phong,
                        @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                        RedirectAttributes ra) {

        // 🌟 LỚP GÁC CỔNG BẢO MẬT: KIỂM TRA RÀNG BUỘC DỮ LIỆU ĐẦU VÀO
        if (phong.getName() == null || phong.getName().trim().isEmpty()) {
            ra.addFlashAttribute("error", "Thêm thất bại: Tên phòng không được phép để trống!");
            return "redirect:/admin/rooms/create";
        }

        if (phong.getPrice() == null || phong.getPrice().compareTo(java.math.BigDecimal.ZERO) < 0) {
            ra.addFlashAttribute("error", "Thêm thất bại: Giá phòng nhập vào không được phép là số âm!");
            return "redirect:/admin/rooms/create"; // Hoặc đường dẫn tương ứng của hàm
        }

        try {
            // 1. Gán giá trị mặc định cho các trường NOT NULL thiếu trong form
            if (phong.getMaTrangThai() == null) {
                phong.setMaTrangThai(1); // 1 = Trống
            }
            if (phong.getSoGiuongPhuToiDa() == null) {
                phong.setSoGiuongPhuToiDa(0);
            }

            // 2. Xử lý file ảnh
            if (imageFile != null && !imageFile.isEmpty()) {
                String fileName = imageFile.getOriginalFilename();
                phong.setImageUrl(fileName);

                String uploadDir = "src/main/resources/static/images/";
                java.nio.file.Path uploadPath = java.nio.file.Paths.get(uploadDir);
                if (!java.nio.file.Files.exists(uploadPath)) {
                    java.nio.file.Files.createDirectories(uploadPath);
                }
                try (java.io.InputStream inputStream = imageFile.getInputStream()) {
                    java.nio.file.Path filePath = uploadPath.resolve(fileName);
                    java.nio.file.Files.copy(inputStream, filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }
            }

            // 3. Thực hiện lưu
            phongRepository.save(phong);
            ra.addFlashAttribute("success", "Thêm phòng mới thành công!");
            return "redirect:/admin/rooms";

        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi lưu phòng: " + e.getMessage());
            return "redirect:/admin/rooms/create";
        }
    }

    // HIỂN THỊ FORM EDIT
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
        Phong phong = phongRepository.findById(id).orElse(null);
        if (phong == null) {
            ra.addFlashAttribute("error", "Không tìm thấy phòng!");
            return "redirect:/admin/rooms";
        }
        model.addAttribute("phong", phong);
        model.addAttribute("listLoai", loaiPhongRepository.findAll());
        setPageTitle(model, "Chỉnh sửa phòng: " + phong.getName());
        return render(model, "view/Admin/Room/edit");
    }

    // XỬ LÝ CẬP NHẬT (ĐÃ BỔ SUNG RÀNG BUỘC)
    @PostMapping("/edit")
    public String update(@ModelAttribute("phong") Phong phong,
                         BindingResult result,
                         @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                         RedirectAttributes ra,
                         Model model) {

        if (result.hasErrors()) {
            System.out.println("===> LOI DỮ LIỆU: " + result.getAllErrors());
            model.addAttribute("listLoai", loaiPhongRepository.findAll());
            return render(model, "view/Admin/Room/edit");
        }

        // 🌟 LỚP GÁC CỔNG BẢO MẬT: KIỂM TRA RÀNG BUỘC KHI CẬP NHẬT
        if (phong.getName() == null || phong.getName().trim().isEmpty()) {
            ra.addFlashAttribute("error", "Cập nhật thất bại: Tên phòng không được phép để trống!");
            return "redirect:/admin/rooms/edit/" + phong.getId();
        }

        if (phong.getPrice() == null || phong.getPrice().compareTo(java.math.BigDecimal.ZERO) < 0) {
            ra.addFlashAttribute("error", "Cập nhật thất bại: Giá phòng nhập vào không được phép là số âm!");
            return "redirect:/admin/rooms/edit/" + phong.getId();
        }

        try {
            Phong phongGoc = phongRepository.findById(phong.getId()).orElse(null);

            if (phongGoc != null) {
                if (phong.getMaTrangThai() == null) phong.setMaTrangThai(phongGoc.getMaTrangThai());
                if (phong.getSoGiuongPhuToiDa() == null) phong.setSoGiuongPhuToiDa(phongGoc.getSoGiuongPhuToiDa());

                if (imageFile != null && !imageFile.isEmpty()) {
                    String fileName = imageFile.getOriginalFilename();
                    phong.setImageUrl(fileName);

                    String uploadDir = "src/main/resources/static/images/";
                    java.nio.file.Path uploadPath = java.nio.file.Paths.get(uploadDir);
                    if (!java.nio.file.Files.exists(uploadPath)) java.nio.file.Files.createDirectories(uploadPath);

                    try (java.io.InputStream inputStream = imageFile.getInputStream()) {
                        java.nio.file.Path filePath = uploadPath.resolve(fileName);
                        java.nio.file.Files.copy(inputStream, filePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    }
                } else {
                    phong.setImageUrl(phongGoc.getImageUrl());
                }

                phongRepository.save(phong);
                ra.addFlashAttribute("success", "Cập nhật thông tin phòng thành công!");
            }

            return "redirect:/admin/rooms";

        } catch (Exception e) {
            System.out.println("===> LOI SQL: " + e.getMessage());
            ra.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/admin/rooms/edit/" + phong.getId();
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteConfirm(@PathVariable("id") Integer id, Model model) {
        Phong phong = phongRepository.findById(id).orElse(null);
        model.addAttribute("phong", phong);
        setPageTitle(model, "Xác nhận xóa phòng");
        return render(model, "view/Admin/Room/delete");
    }

    // DELETE
    @PostMapping("/delete")
    public String destroy(@RequestParam("id") Integer id, RedirectAttributes ra) {
        try {
            phongRepository.deleteById(id);
            ra.addFlashAttribute("success", "Đã xóa phòng thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Không thể xóa phòng này (có thể đang có dữ liệu liên quan)");
        }
        return "redirect:/admin/rooms";
    }
}