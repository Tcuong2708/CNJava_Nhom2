package com.mayhotel.web_khachsan_nhom2.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "Phong")
public class Phong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // IDENTITY(1,1)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "Name", nullable = false, length = 100) // nvarchar(100) NOT NULL
    private String name;

    @Column(name = "Price", nullable = false) // decimal(18, 0) NOT NULL
    private BigDecimal price;

    @Column(name = "Detail", columnDefinition = "NVARCHAR(MAX)") // nvarchar(max)
    private String detail;

    @Column(name = "ImageUrl", length = 255) // nvarchar(255)
    private String imageUrl;

    @Column(name = "MaLoai", nullable = false) // int NOT NULL (Khóa ngoại)
    private Integer maLoai;

    @Column(name = "MaTrangThai", nullable = false) // int NOT NULL, mặc định là 1
    private Integer maTrangThai;

    @Column(name = "GhiChu", length = 200) // nvarchar(200)
    private String ghiChu;

    @Column(name = "SoGiuongPhuToiDa") // int, mặc định là 0
    private Integer soGiuongPhuToiDa;

    // --- Constructors ---
    public Phong() {
    }

    // --- Getters and Setters ---
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getMaLoai() {
        return maLoai;
    }

    public void setMaLoai(Integer maLoai) {
        this.maLoai = maLoai;
    }

    public Integer getMaTrangThai() {
        return maTrangThai;
    }

    public void setMaTrangThai(Integer maTrangThai) {
        this.maTrangThai = maTrangThai;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public Integer getSoGiuongPhuToiDa() {
        return soGiuongPhuToiDa;
    }

    public void setSoGiuongPhuToiDa(Integer soGiuongPhuToiDa) {
        this.soGiuongPhuToiDa = soGiuongPhuToiDa;
    }
}