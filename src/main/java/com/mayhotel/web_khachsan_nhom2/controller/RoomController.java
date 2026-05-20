package com.mayhotel.web_khachsan_nhom2.controller;

import com.mayhotel.web_khachsan_nhom2.model.Phong;
import com.mayhotel.web_khachsan_nhom2.repository.PhongRepository;
import com.mayhotel.web_khachsan_nhom2.repository.LoaiPhongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
public class RoomController extends BaseController {

    @Autowired
    private PhongRepository phongRepository;

    @Autowired
    private LoaiPhongRepository loaiPhongRepository;

    @GetMapping({"/rooms", "/rooms/all"})
    public String listRooms(
            @RequestParam(value = "maLoai", required = false) Integer maLoai,
            @RequestParam(value = "priceRange", defaultValue = "") String priceRange,
            @RequestParam(value = "searchString", defaultValue = "") String searchString,
            Model model) {

        setPageTitle(model, "Danh sách phòng nghỉ");
        setExtraCSS(model, "view/Rooms/list :: extra_css");

        // 1. Xử lý khoảng giá
        BigDecimal minPrice = null;
        BigDecimal maxPrice = null;

        if (priceRange.equals("lt500")) {
            maxPrice = new BigDecimal(500000);
        } else if (priceRange.equals("500-1000")) {
            minPrice = new BigDecimal(500000);
            maxPrice = new BigDecimal(1000000);
        } else if (priceRange.equals("gt2000")) {
            minPrice = new BigDecimal(2000000);
        }

        // 2. Chuẩn hóa chuỗi tìm kiếm
        String search = (searchString == null || searchString.isEmpty()) ? null : searchString;

        // 3. Truy vấn dữ liệu có lọc
        List<Phong> listPhong = phongRepository.findWithFilters(search, maLoai, minPrice, maxPrice);

        // 4. Đưa dữ liệu ra View
        model.addAttribute("listPhong", listPhong);
        model.addAttribute("listLoai", loaiPhongRepository.findAll()); // Để hiện sidebar

        // Giữ lại trạng thái filter trên giao diện
        model.addAttribute("currentMaLoai", maLoai);
        model.addAttribute("currentPriceRange", priceRange);
        model.addAttribute("searchString", searchString);

        return render(model, "view/Rooms/list");
    }

    @GetMapping("/rooms/{id}")
    public String roomDetail(@PathVariable("id") Integer id, Model model) {
        Optional<Phong> phongOpt = phongRepository.findById(id);
        if (phongOpt.isPresent()) {
            Phong phong = phongOpt.get();
            setPageTitle(model, "Chi tiết phòng: " + phong.getName());
            model.addAttribute("phong", phong);
            return render(model, "view/Rooms/detail");
        }
        return "redirect:/rooms";
    }
}