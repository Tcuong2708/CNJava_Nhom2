package com.mayhotel.web_khachsan_nhom2.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "Loai") // Ánh xạ với bảng Loai trong SQL
public class LoaiPhong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaLoai") // Khóa chính MaLoai
    private Integer maLoai;

    @Column(name = "Name", nullable = false, length = 100) // Cột Name
    private String name;

    @Column(name = "SoNguoi") // Cột SoNguoi
    private Integer soNguoi;

    // Quan hệ 1-N với bảng Phong
    @OneToMany(mappedBy = "maLoai", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Phong> phongs = new ArrayList<>();

    // --- Constructors ---
    public LoaiPhong() {
    }

    public LoaiPhong(String name, Integer soNguoi) {
        this.name = name;
        this.soNguoi = soNguoi;
    }

    // --- Business Logic: Lấy danh sách các phòng còn Trống ---
    public List<Phong> getPhongsTrong() {
        if (this.phongs == null) {
            return new ArrayList<>();
        }
        // Lọc các phòng có MaTrangThai = 1 (Trống) dựa trên bảng TrangThaiPhong
        return this.phongs.stream()
                .filter(p -> p.getMaTrangThai() != null && p.getMaTrangThai() == 1)
                .collect(Collectors.toList());
    }

    // --- Getters and Setters ---
    public Integer getMaLoai() {
        return maLoai;
    }

    public void setMaLoai(Integer maLoai) {
        this.maLoai = maLoai;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSoNguoi() {
        return soNguoi;
    }

    public void setSoNguoi(Integer soNguoi) {
        this.soNguoi = soNguoi;
    }

    public List<Phong> getPhongs() {
        return phongs;
    }

    public void setPhongs(List<Phong> phongs) {
        this.phongs = phongs;
    }
}