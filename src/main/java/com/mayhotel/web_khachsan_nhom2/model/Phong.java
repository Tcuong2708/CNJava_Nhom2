package com.mayhotel.web_khachsan_nhom2.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "Phong")
@Data
public class Phong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @Column(name = "Name", nullable = false, length = 100)
    private String name;

    @Column(name = "Price", nullable = false)
    private BigDecimal price;

    @Column(name = "Detail", columnDefinition = "NVARCHAR(MAX)")
    private String detail;

    @Column(name = "ImageUrl", length = 255)
    private String imageUrl;

    @Column(name = "MaLoai", nullable = false)
    private Integer maLoai;

    @Column(name = "MaTrangThai", nullable = false)
    private Integer maTrangThai;

    @Column(name = "GhiChu", length = 200)
    private String ghiChu;

    @Column(name = "SoGiuongPhuToiDa")
    private Integer soGiuongPhuToiDa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaLoai", insertable = false, updatable = false)
    @JsonIgnore
    private LoaiPhong loaiPhong;

    public Phong() {
    }
}