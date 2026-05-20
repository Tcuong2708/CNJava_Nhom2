package com.mayhotel.web_khachsan_nhom2.controller;

import com.mayhotel.web_khachsan_nhom2.model.LoaiPhong;
import com.mayhotel.web_khachsan_nhom2.repository.LoaiPhongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpSession;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController extends BaseController {

    @Autowired
    private LoaiPhongRepository loaiPhongRepository;

    @GetMapping("")
    public String index(Model model) {
        setPageTitle(model, "Quản lý loại phòng");
        List<LoaiPhong> list = loaiPhongRepository.findAll();
        model.addAttribute("categories", list);

        setExtraCSS(model, "view/Admin/Category/index :: extra_css");
        return render(model, "view/Admin/Category/index");
    }

    @GetMapping("/create")
    public String create(Model model) {
        setPageTitle(model, "Thêm loại phòng");
        model.addAttribute("loaiPhong", new LoaiPhong());

        setExtraCSS(model, "view/Admin/Category/create :: extra_css");
        return render(model, "view/Admin/Category/create");
    }

    @PostMapping("/create")
    public String postCreate(@ModelAttribute("loaiPhong") LoaiPhong loaiPhong, RedirectAttributes redirectAttributes) {
        try {
            loaiPhongRepository.save(loaiPhong);
            redirectAttributes.addFlashAttribute("success", "Thêm loại phòng mới thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi thêm mới!");
        }
        return "redirect:/admin/categories";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<LoaiPhong> loaiOpt = loaiPhongRepository.findById(id);
        if (loaiOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy loại phòng!");
            return "redirect:/admin/categories";
        }
        setPageTitle(model, "Cập nhật loại phòng");
        model.addAttribute("loaiPhong", loaiOpt.get());

        setExtraCSS(model, "view/Admin/Category/edit :: extra_css");
        return render(model, "view/Admin/Category/edit");
    }

    @PostMapping("/edit")
    public String postEdit(@ModelAttribute("loaiPhong") LoaiPhong loaiPhong, RedirectAttributes redirectAttributes) {
        try {
            Optional<LoaiPhong> existingOpt = loaiPhongRepository.findById(loaiPhong.getMaLoai());
            if (existingOpt.isPresent()) {
                LoaiPhong existing = existingOpt.get();
                existing.setName(loaiPhong.getName());
                loaiPhongRepository.save(existing);
                redirectAttributes.addFlashAttribute("success", "Cập nhật loại phòng thành công!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi cập nhật!");
        }
        return "redirect:/admin/categories";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Optional<LoaiPhong> loaiOpt = loaiPhongRepository.findById(id);
        if (loaiOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy loại phòng!");
            return "redirect:/admin/categories";
        }
        setPageTitle(model, "Xác nhận xóa");
        model.addAttribute("loaiPhong", loaiOpt.get());

        setExtraCSS(model, "view/Admin/Category/delete :: extra_css");
        return render(model, "view/Admin/Category/delete");
    }

    @PostMapping("/delete/{id}")
    public String postDelete(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            loaiPhongRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Xóa loại phòng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Không thể xóa loại phòng này do đã có phòng thuộc nhóm này!");
        }
        return "redirect:/admin/categories";
    }
}