package com.mayhotel.web_khachsan_nhom2.interceptor;

import com.mayhotel.web_khachsan_nhom2.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminSecurityInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            System.out.println("-> [BẢO MẬT GÁC CỔNG]: Từ chối truy cập! Phiên đăng nhập đã hết hạn hoặc chưa tồn tại.");

            // Điều hướng về trang login kèm tham số báo lỗi hết thời gian chờ phiên
            response.sendRedirect(request.getContextPath() + "/account/login?error=timeout");
            return false;
        }

        Integer role = currentUser.getRoleID();

        // Điều kiện: Chỉ cho phép Admin (1) và Nhân viên (2) được quyền đi tiếp vào vùng /admin/**
        if (role == null || (role != 1 && role != 2)) {
            System.out.println("-> [CẢNH BÁO PHÂN QUYỀN]: Tài khoản '" + currentUser.getTenDangNhap()
                    + "' với RoleID=[" + role + "] cố ý truy cập trái phép vùng Quản trị!");

            response.sendRedirect(request.getContextPath() + "/account/login?error=denied");
            return false;
        }

        return true;
    }
}