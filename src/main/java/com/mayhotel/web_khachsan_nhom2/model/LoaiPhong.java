package com.mayhotel.web_khachsan_nhom2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "Loai")
@Data
public class LoaiPhong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaLoai")
    private Integer maLoai;

    @Column(name = "Name", nullable = false, length = 100) // Cột Name
    private String name;

    @Column(name = "SoNguoi")
    private Integer soNguoi;

    // Quan hệ 1-N với bảng Phong
    @OneToMany(mappedBy = "loaiPhong", cascade = CascadeType.ALL, fetch = FetchType.EAGER)    @JsonIgnore
    private List<Phong> phongs = new ArrayList<>();

    public LoaiPhong() {
    }

    public LoaiPhong(String name, Integer soNguoi) {
        this.name = name;
        this.soNguoi = soNguoi;
    }

    public List<Phong> getPhongsTrong() {
        if (this.phongs == null) {
            return new ArrayList<>();
        }
        return this.phongs.stream()
                .filter(p -> p.getMaTrangThai() != null && p.getMaTrangThai() == 1)
                .collect(Collectors.toList());
    }

    public BigDecimal getPrice() {
        if (this.phongs == null || this.phongs.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return this.phongs.get(0).getPrice();
    }
}