package com.mayhotel.web_khachsan_nhom2.repository;

import com.mayhotel.web_khachsan_nhom2.model.PhieuDat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PhieuDatRepository extends JpaRepository<PhieuDat, Integer> {
    @Query(value = "SELECT MONTH(NgayDat) AS Thang, SUM(TongTien) AS DoanhThu " +
            "FROM HoaDon " +
            "WHERE DaThanhToan = 1 AND YEAR(NgayDat) = :year " +
            "GROUP BY MONTH(NgayDat) " +
            "ORDER BY MONTH(NgayDat)", nativeQuery = true)
    List<Object[]> getMonthlyRevenueByYear(@Param("year") int year);

    List<PhieuDat> findByIdTaiKhoan(Integer idTaiKhoan);

    List<PhieuDat> findBySdt(String sdt);
}