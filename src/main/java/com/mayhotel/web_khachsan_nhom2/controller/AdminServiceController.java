package com.mayhotel.web_khachsan_nhom2.controller;

import com.mayhotel.web_khachsan_nhom2.model.DichVu;
import com.mayhotel.web_khachsan_nhom2.repository.DichVuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/services")
public class AdminServiceController extends BaseController {

    @Autowired
    private DichVuRepository dichVuRepository;

    // 1. TRANG DANH SÁCH DỊCH VỤ (Index)
    @GetMapping("")
    public String index(Model model) {
        setPageTitle(model, "Quản lý dịch vụ");
        List<DichVu> list = dichVuRepository.findAll();
        model.addAttribute("services", list);

        setExtraCSS(model, "view/Admin/Service/index :: extra_css");
        return render(model, "view/Admin/Service/index");
    }

    // 2. TRANG THÊM MỚI DỊCH VỤ (GET)
    @GetMapping("/create")
    public String create(Model model) {
        setPageTitle(model, "Thêm dịch vụ");
        model.addAttribute("dichVu", new DichVu());

        setExtraCSS(model, "view/Admin/Service/create :: extra_css");
        return render(model, "view/Admin/Service/create");
    }

    // 3. XỬ LÝ THÊM MỚI DỊCH VỤ (POST) [cite: 12]
    @PostMapping("/create")
    public String postCreate(@ModelAttribute("dichVu") DichVu dichVu, RedirectAttributes redirectAttributes) {
        try {
            dichVuRepository.save(dichVu);
            redirectAttributes.addFlashAttribute("success", "Thêm dịch vụ mới thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi lưu dịch vụ!");
        }
        return "redirect:/admin/services";
    }

    // 4. TRANG CẬP NHẬT DỊCH VỤ (GET)
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<DichVu> dvOpt = dichVuRepository.findById(id);
        if (dvOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy dịch vụ yêu cầu!");
            return "redirect:/admin/services";
        }
        setPageTitle(model, "Cập nhật dịch vụ");
        model.addAttribute("dichVu", dvOpt.get());

        setExtraCSS(model, "view/Admin/Service/edit :: extra_css");
        return render(model, "view/Admin/Service/edit");
    }

    // 5. XỬ LÝ CẬP NHẬT DỊCH VỤ (POST) [cite: 54]
    @PostMapping("/edit")
    public String postEdit(@ModelAttribute("dichVu") DichVu dichVu, RedirectAttributes redirectAttributes) {
        try {
            Optional<DichVu> existingOpt = dichVuRepository.findById(dichVu.getMaDV());
            if (existingOpt.isPresent()) {
                DichVu existing = existingOpt.get();
                existing.setTenDV(dichVu.getTenDV());
                existing.setGiaTien(dichVu.getGiaTien());
                existing.setDonVi(dichVu.getDonVi());

                dichVuRepository.save(existing);
                redirectAttributes.addFlashAttribute("success", "Cập nhật dịch vụ thành công!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi cập nhật dữ liệu!");
        }
        return "redirect:/admin/services";
    }

    // 6. TRANG XÁC NHẬN XÓA DỊCH VỤ (GET)
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<DichVu> dvOpt = dichVuRepository.findById(id);
        if (dvOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy dịch vụ cần xóa!");
            return "redirect:/admin/services";
        }
        setPageTitle(model, "Xác nhận xóa dịch vụ");
        model.addAttribute("dichVu", dvOpt.get());

        setExtraCSS(model, "view/Admin/Service/delete :: extra_css");
        return render(model, "view/Admin/Service/delete");
    }

    // 7. XỬ LÝ XÓA VĨNH VIỄN (POST) [cite: 39]
    @PostMapping("/delete/{id}")
    public String postDelete(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            dichVuRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Xóa dịch vụ thành công vĩnh viễn!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa dịch vụ này do đang dính dữ liệu hóa đơn!");
        }
        return "redirect:/admin/services";
    }
}