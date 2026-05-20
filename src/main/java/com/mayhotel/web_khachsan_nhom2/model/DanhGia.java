package com.mayhotel.web_khachsan_nhom2.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "DanhGia")
public class DanhGia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "User_ID")
    private User user;

    @Column(name = "TenPhong")
    private String tenPhong;

    @Column(name = "NoiDung", length = 1000)
    private String noiDung;

    @Column(name = "SoSao")
    private Integer soSao;

    @Column(name = "NgayDanhGia")
    private LocalDateTime ngayDanhGia;

    @Column(name = "TrangThai")
    private Integer trangThai; // 0: Chờ duyệt, 1: Đã duyệt lên hiển thị công khai

}