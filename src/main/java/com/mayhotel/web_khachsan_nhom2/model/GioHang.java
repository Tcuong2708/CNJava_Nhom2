package com.mayhotel.web_khachsan_nhom2.model;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class GioHang {
    private List<CartItem> items = new ArrayList<>();

    // Thêm hoặc cộng dồn số lượng nếu trùng Loại Phòng và ngày
    public void add(CartItem newItem) {
        for (CartItem item : items) {
            if (item.getLoaiPhong().getMaLoai().equals(newItem.getLoaiPhong().getMaLoai())) {
                item.setSoLuong(item.getSoLuong() + newItem.getSoLuong());
                return;
            }
        }
        items.add(newItem);
    }

    // Xóa khỏi giỏ hàng
    public void remove(int maLoai) {
        items.removeIf(item -> item.getLoaiPhong().getMaLoai() == maLoai);
    }

    // Tính tổng tiền toàn bộ giỏ hàng
    public java.math.BigDecimal getTongTienGioHang() {
        return items.stream()
                .map(CartItem::getThanhTien)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }

    //Cập nhật số lượng
    public int getTongSoLuong() {
        if (items == null) return 0;
        return items.stream().mapToInt(CartItem::getSoLuong).sum();
    }
}