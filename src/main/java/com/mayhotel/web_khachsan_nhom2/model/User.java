package com.mayhotel.web_khachsan_nhom2.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Account")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDTaiKhoan")
    private Integer idTaiKhoan;

    @Column(name = "TenDangNhap")
    private String tenDangNhap;

    @Column(name = "MatKhau")
    private String matKhau;

    @Column(name = "HoTen", columnDefinition = "NVARCHAR(255)")
    private String hoTen;

    @Column(name = "SoDienThoai")
    private String soDienThoai;

    @Column(name = "DiaChi", columnDefinition = "NVARCHAR(500)")
    private String diaChi;

    @Column(name = "RoleID")
    private Integer roleID;

    @Column(name = "QuocTich", columnDefinition = "NVARCHAR(100)")
    private String quocTich;

}