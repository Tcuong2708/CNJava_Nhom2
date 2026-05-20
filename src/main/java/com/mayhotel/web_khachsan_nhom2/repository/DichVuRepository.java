package com.mayhotel.web_khachsan_nhom2.repository;

import com.mayhotel.web_khachsan_nhom2.model.DichVu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DichVuRepository extends JpaRepository<DichVu, Integer> {
}