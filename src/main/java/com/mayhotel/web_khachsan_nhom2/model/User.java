package com.mayhotel.web_khachsan_nhom2.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Acoount")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IDTaiKhoan")
    private Integer IDTaiKhoan;

    @Column(name = "TenDangNhap")
    private String tenDangNhap;

    @Column(name = "MatKhau")
    private String matKhau;

    @Column(name = "SoDienThoai")
    private String soDienThoai;

    @Column(name = "DiaChi")
    private String DiaChi;

    @Column(name = "RoleID")
    private Integer roleID;

    @Column(name = "QuocTich")
    private String QuocTich;
}
