package com.mayhotel.web_khachsan_nhom2.repository;

import com.mayhotel.web_khachsan_nhom2.model.Phong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PhongRepository extends JpaRepository<Phong, Integer> {
    @Query("SELECT p FROM Phong p WHERE " +
            "(:search IS NULL OR p.name LIKE %:search%) AND " +
            "(:maLoai IS NULL OR p.maLoai = :maLoai) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice)")
    List<Phong> findWithFilters(@Param("search") String search,
                                @Param("maLoai") Integer maLoai,
                                @Param("minPrice") BigDecimal minPrice,
                                @Param("maxPrice") BigDecimal maxPrice);
}