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
            response.sendRedirect(request.getContextPath() + "/account/login?error=timeout");
            return false;
        }

        Integer role = currentUser.getRoleID();
        if (role == null || (role != 1 && role != 2)) {
            response.sendRedirect(request.getContextPath() + "/account/login?error=denied");
            return false;
        }

        return true;
    }
}