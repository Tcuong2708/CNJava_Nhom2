package com.mayhotel.web_khachsan_nhom2.config;

import com.mayhotel.web_khachsan_nhom2.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");

        if (currentUser == null) {
            System.out.println("-> [BẢO MẬT GÁC CỔNG]: Phát hiện truy cập trái phép! Chưa có phiên Session.");
            response.sendRedirect(request.getContextPath() + "/account/login");
            return false;
        }

        // AUTHORIZATION
        if (currentUser.getRoleID() != null && currentUser.getRoleID() == 1) {
            return true;
        }

        System.out.println("-> [BẢO MẬT PHÂN QUYỀN]: Tài khoản '" + currentUser.getTenDangNhap() + "' cố tình chiếm quyền Admin!");

        session.setAttribute("errorSession", "Cảnh báo bảo mật: Bạn không có quyền truy cập vào phân hệ dành cho Ban Quản Trị!");

        response.sendRedirect(request.getContextPath() + "/");
        return false;
    }
}