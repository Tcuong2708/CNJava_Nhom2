package com.mayhotel.web_khachsan_nhom2.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // =========================================================================
    // 1. 🌟 HÀM MỚI: GỬI MÃ OTP XÁC THỰC (GIÚP HẾT BÁO ĐỎ Ở USER_SERVICE)
    // =========================================================================
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("email_gui_cua_cuong@gmail.com", "MAY HOTEL LUXURY");
            helper.setTo(toEmail);
            helper.setSubject("Mã OTP Xác Thực Giao Dịch Bảo Mật - MAY HOTEL");

            String htmlContent = "<div style=\"font-family: Arial, sans-serif; max-width: 500px; margin: 0 auto; border: 1px solid #e0e0e0; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.05);\">"
                    + "  <div style=\"background-color: #0F2942; padding: 20px; text-align: center;\">"
                    + "    <h2 style=\"color: #C5A017; margin: 0; text-transform: uppercase; letter-spacing: 1px; font-size: 20px;\">MAY HOTEL SECURITY</h2>"
                    + "  </div>"
                    + "  <div style=\"padding: 30px; background-color: #ffffff; color: #333333; line-height: 1.6;\">"
                    + "    <p style=\"font-size: 15px; margin-top: 0;\">Xin chào,</p>"
                    + "    <p>Hệ thống bảo mật của <strong>May Hotel</strong> vừa nhận được yêu cầu xác thực hành động từ bạn (Đăng ký tài khoản mới hoặc Khôi phục thay đổi mật khẩu).</p>"
                    + "    <p>Vui lòng sử dụng mã OTP an toàn dưới đây để hoàn tất thủ tục:</p>"
                    + "    "
                    + "    <div style=\"text-align: center; margin: 25px 0;\">"
                    + "      <div style=\"display: inline-block; background-color: #f4f6f8; border: 2px dashed #C5A017; padding: 12px 35px; font-size: 26px; font-weight: bold; color: #0F2942; letter-spacing: 5px; border-radius: 6px;\">"
                    +         otp
                    + "      </div>"
                    + "    </div>"
                    + "    "
                    + "    <p style=\"color: #dc3545; font-size: 13px; font-style: italic;\"> Mã số xác thực này có hiệu lực trong vòng 5 phút và chỉ sử dụng được một lần duy nhất. Tuyệt đối không cung cấp mã này cho bất kỳ ai.</p>"
                    + "    <hr style=\"border: none; border-top: 1px solid #eeeeee; margin: 20px 0;\">"
                    + "    <small style=\"color: #999999; display: block; line-height: 1.4;\">Đây là hòm thư tự động gác cổng hệ thống. Vui lòng không phản hồi lại email này.</small>"
                    + "  </div>"
                    + "  <div style=\"background-color: #f8f9fa; padding: 12px; text-align: center; font-size: 11px; color: #999999; border-top: 1px solid #eeeeee;\">"
                    + "    © 2026 May Hotel Group. All rights reserved."
                    + "  </div>"
                    + "</div>";

            helper.setText(htmlContent, true);

            System.out.println("-> [SMTP OTP]: Đang phát mã OTP thật tới hòm thư: " + toEmail);
            mailSender.send(message);
            System.out.println("-> [SMTP OTP SUCCESS]: Mã OTP gửi thành công!");

        } catch (Exception e) {
            System.err.println("🚨 [LỖI TẠI TẦNG EMAIL_SERVICE - HÀM OTP]:");
            e.printStackTrace();
        }
    }

    // =========================================================================
    // 2. HÀM CŨ CỦA CƯỜNG: GỬI THƯ CHÀO MỪNG THÀNH VIÊN (WELCOME LETTER)
    // =========================================================================
    public void sendWelcomeEmail(String toEmail, String clientName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("email_gui_cua_cuong@gmail.com", "MAY HOTEL");
            helper.setTo(toEmail);
            helper.setSubject("Chào mừng bạn đến với Hệ thống Đặt phòng MAY HOTEL");

            String htmlContent = "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; border: 1px solid #eee; border-radius: 8px; overflow: hidden;\">"
                    + "  <div style=\"background-color: #0F2942; padding: 24px; text-align: center;\">"
                    + "    <h1 style=\"color: #C5A017; margin: 0; text-transform: uppercase; letter-spacing: 1px; font-size: 22px;\">MAY HOTEL LUXURY</h1>"
                    + "  </div>"
                    + "  <div style=\"padding: 30px; background-color: #ffffff; color: #333333; line-height: 1.6;\">"
                    + "    <p style=\"font-size: 16px; margin-top: 0;\">Xin chào <strong>" + clientName + "</strong>,</p>"
                    + "    <p>Chúc mừng bạn đã đăng ký tài khoản thành viên thành công trên hệ thống quản lý lưu trú của <strong>May Hotel</strong>.</p>"
                    + "    <p>Từ bây giờ, bạn đã có thể đăng nhập vào hệ thống để trải nghiệm dịch vụ đặt phòng trực tuyến, đặt các dịch vụ đi kèm cao cấp và nhận các chương trình ưu đãi đặc quyền dành riêng cho thành viên VIP.</p>"
                    + "    <div style=\"text-align: center; margin: 30px 0;\">"
                    + "      <a href=\"http://localhost:8080/account/login\" style=\"background-color: #C5A017; color: white; padding: 12px 30px; text-decoration: none; font-weight: bold; border-radius: 4px; display: inline-block; text-transform: uppercase; font-size: 14px;\">Đăng Nhập Ngay</a>"
                    + "    </div>"
                    + "    <hr style=\"border: none; border-top: 1px solid #eeeeee; margin: 20px 0;\">"
                    + "    <small style=\"color: #777777;\">Đây là email tự động gửi từ hệ thống. Vui lòng không phản hồi lại thư này. Nếu có bất kỳ thắc mắc nào, vui lòng liên hệ hotline lễ tân tại quầy của khách sạn.</small>"
                    + "  </div>"
                    + "  <div style=\"background-color: #f8f9fa; padding: 15px; text-align: center; font-size: 12px; color: #999999; border-top: 1px solid #eeeeee;\">"
                    + "    © 2026 May Hotel Group. All rights reserved."
                    + "  </div>"
                    + "</div>";

            helper.setText(htmlContent, true);

            System.out.println("-> [WELCOME EMAIL]: Đang gửi thư chào mừng đến: " + toEmail);
            mailSender.send(message);
            System.out.println("-> [WELCOME EMAIL SUCCESS]: Gửi thư chào mừng thành công!");

        } catch (Exception e) {
            System.err.println("🚨 [LỖI TẠI TẦNG EMAIL_SERVICE - HÀM WELCOME]:");
            e.printStackTrace();
        }
    }
}