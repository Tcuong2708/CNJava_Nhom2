package com.mayhotel.web_khachsan_nhom2.model;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CartItem {
    private LoaiPhong loaiPhong;
    private int soLuong;
    private LocalDate ngayNhan;
    private LocalDate ngayTra;

    // Hàm tính số đêm ở
    public long getSoDem() {
        if (ngayNhan != null && ngayTra != null && ngayTra.isAfter(ngayNhan)) {
            return java.time.temporal.ChronoUnit.DAYS.between(ngayNhan, ngayTra);
        }
        return 0;
    }

    // Hàm tính tổng tiền của Item này
    public java.math.BigDecimal getThanhTien() {
        if (loaiPhong == null || loaiPhong.getPrice() == null) return java.math.BigDecimal.ZERO;
        return loaiPhong.getPrice().multiply(new java.math.BigDecimal(soLuong * getSoDem()));
    }
}