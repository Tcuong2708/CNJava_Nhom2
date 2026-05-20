package com.mayhotel.web_khachsan_nhom2.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Account")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDTaiKhoan")
    private Integer idTaiKhoan;

    @Column(name = "TenDangNhap", nullable = false)
    private String tenDangNhap;

    @Column(name = "MatKhau", nullable = false)
    private String matKhau;

    @Column(name = "HoTen", columnDefinition = "NVARCHAR(255)")
    private String hoTen;

    // BẮT BUỘC PHẢI CÓ ĐỂ CHẠY OTP
    @Column(name = "Email", unique = true)
    private String email;

    @Column(name = "SoDienThoai")
    private String soDienThoai;

    @Column(name = "DiaChi", columnDefinition = "NVARCHAR(500)")
    private String diaChi;

    @Column(name = "QuocTich", columnDefinition = "NVARCHAR(100)")
    private String quocTich;

    @Column(name = "RoleID")
    private Integer roleID;

    @Column(name = "TrangThai")
    private Boolean trangThai = true;
}