package com.mayhotel.web_khachsan_nhom2.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "DichVu")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DichVu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaDV")
    private Integer maDV;

    @Column(name = "TenDV", nullable = false, columnDefinition = "NVARCHAR(255)") //
    private String tenDV;

    @Column(name = "GiaTien", nullable = false) // Sử dụng BigDecimal để đồng bộ tính toán tiền tệ
    private BigDecimal giaTien;

    @Column(name = "DonVi", columnDefinition = "NVARCHAR(100)") // Đơn vị tính: Lần, Suất, Kg...
    private String donVi;
}