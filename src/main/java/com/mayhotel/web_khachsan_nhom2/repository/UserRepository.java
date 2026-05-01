package com.mayhotel.web_khachsan_nhom2.repository;

import com.mayhotel.web_khachsan_nhom2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // Kiểu Integer ở đây khớp với Integer của idTaiKhoan
    User findByTenDangNhap(String tenDangNhap);
}