package com.mayhotel.web_khachsan_nhom2.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import org.hibernate.annotations.Nationalized;

import java.math.BigDecimal;

@Entity
@Table(name = "HoaDon")
@Data
public class PhieuDat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaHD")
    private Integer id;

    @Column(name = "HoTen")
    @Nationalized
    private String hoTen;

    @Column(name = "DienThoai")
    private String sdt;

    @Column(name = "DiaChi")
    @Nationalized
    private String diaChi;

    @Column(name = "NgayDat")
    private LocalDateTime ngayDat = LocalDateTime.now();

    @Column(name = "NgayNhan")
    private LocalDate ngayCheckIn;

    @Column(name = "NgayTra")
    private LocalDate ngayCheckOut;

    @Column(name = "TongTien")
    private BigDecimal totalPrice;

    @Column(name = "IDTaiKhoan")
    private Integer idTaiKhoan;

    @Column(name = "MaPhong")
    private Integer maPhong;

    @Column(name = "DaThanhToan")
    private Boolean daThanhToan = false;

    @Column(name = "PhuThu")
    private BigDecimal phuThu = BigDecimal.ZERO;


    @Column(name = "GhiChu", length = 1000)
    @Nationalized
    private String ghiChu;

    @Column(name = "PhuongThucThanhToan", length = 50)
    @Nationalized
    private String phuongThucThanhToan;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaPhong", insertable = false, updatable = false)
    private Phong phong;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IDTaiKhoan", insertable = false, updatable = false)
    private User user;
}