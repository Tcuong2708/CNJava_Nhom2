package com.mayhotel.web_khachsan_nhom2.controller;

import com.mayhotel.web_khachsan_nhom2.model.PhieuDat;
import com.mayhotel.web_khachsan_nhom2.repository.PhieuDatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin/statistical")
public class AdminStatisticalController extends BaseController {

    @Autowired
    private PhieuDatRepository phieuDatRepository;

    @GetMapping("")
    public String index(Model model) {
        int currentYear = 2026; // Đồng bộ theo mốc thời gian thực tế của hệ thống
        setPageTitle(model, "Thống kê doanh thu");

        double[] monthlyRevenue = new double[12];
        int[] monthlyOrders = new int[12];

        // Lấy tất cả phiếu đặt phòng từ CSDL 
        List<PhieuDat> allOrders = phieuDatRepository.findAll();

        double totalRevenue = 0;
        int totalOrders = 0;

        // Phân loại dữ liệu theo từng tháng của năm 2026
        for (PhieuDat phieu : allOrders) {
            if (phieu.getNgayDat() != null && phieu.getNgayDat().getYear() == currentYear) {
                int monthIndex = phieu.getNgayDat().getMonthValue() - 1; // Tháng 1-12 chuyển thành Index 0-11

                if (phieu.getTotalPrice() != null) {
                    double price = phieu.getTotalPrice().doubleValue();
                    monthlyRevenue[monthIndex] += price;
                    totalRevenue += price;
                }

                monthlyOrders[monthIndex] += 1;
                totalOrders += 1;
            }
        }

        double avgRevenuePerMonth = totalRevenue / 12;

        model.addAttribute("nam", currentYear);
        model.addAttribute("tongDoanhThu", totalRevenue);
        model.addAttribute("tongSoDon", totalOrders);
        model.addAttribute("trungBinhThang", avgRevenuePerMonth);

        model.addAttribute("doanhThuArr", monthlyRevenue);
        model.addAttribute("soDonArr", monthlyOrders);

        return render(model, "view/Admin/Statistical/index");
    }
}