package com.mayhotel.web_khachsan_nhom2.controller;

import com.mayhotel.web_khachsan_nhom2.model.Phong;
import com.mayhotel.web_khachsan_nhom2.repository.PhongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin/api/rooms")
public class AdminRoomApiController extends BaseController {

    @Autowired
    private PhongRepository phongRepository;

    // =========================================================================
    // 1. TRẢ VỀ GIAO DIỆN KIỂM THỬ TRỰC TIẾP TRÊN TRÌNH DUYỆT (HTML)
    // =========================================================================
    @GetMapping("")
    public String index(Model model) {
        setPageTitle(model, "API Room Playground");
        setExtraCSS(model, "view/Admin/RoomApi/index :: extra_css");
        return render(model, "view/Admin/RoomApi/index");
    }

    // =========================================================================
    // 2. TRẢ VỀ CHUỖI JSON TOÀN BỘ DANH SÁCH PHÒNG
    // =========================================================================
    @GetMapping("/json")
    @ResponseBody
    public ResponseEntity<List<Phong>> getAllRoomsJson() {
        List<Phong> rooms = phongRepository.findAll();
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    // =========================================================================
    // 3. LẤY CHI TIẾT MỘT PHÒNG QUA MÃ SỐ (GET BY ID)
    // =========================================================================
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Object> getRoomById(@PathVariable("id") Integer id) {
        Optional<Phong> phongOpt = phongRepository.findById(id);
        if (phongOpt.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Không tìm thấy phòng có ID số: " + id);
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(phongOpt.get(), HttpStatus.OK);
    }

    // =========================================================================
    // 4. THÊM MỚI PHÒNG (POST METHOD)
    // =========================================================================
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createRoom(@RequestBody Phong newPhong) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (newPhong.getMaTrangThai() == null) {
                newPhong.setMaTrangThai(1); // 1 = Mặc định phòng trống
            }
            if (newPhong.getMaLoai() == null) {
                newPhong.setMaLoai(1); // Gán mặc định vào danh mục loại phòng 1
            }
            if (newPhong.getSoGiuongPhuToiDa() == null) {
                newPhong.setSoGiuongPhuToiDa(0);
            }

            // Ép đồng bộ đẩy trực tiếp xuống SQL Server lập tức
            Phong savedPhong = phongRepository.saveAndFlush(newPhong);

            response.put("status", "SUCCESS");
            response.put("message", "Thêm mới phòng thông qua API POST thành công!");
            response.put("data", savedPhong);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Lỗi xử lý lưu dữ liệu API: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // =========================================================================
    // 5. CẬP NHẬT THÔNG TIN PHÒNG (PUT METHOD)
    // =========================================================================
    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateRoom(@PathVariable("id") Integer id, @RequestBody Phong updatedData) {
        Map<String, Object> response = new HashMap<>();
        Optional<Phong> existingOpt = phongRepository.findById(id);

        if (existingOpt.isEmpty()) {
            response.put("status", "ERROR");
            response.put("message", "Mã phòng yêu cầu chỉnh sửa không tồn tại!");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        try {
            Phong existingPhong = existingOpt.get();
            existingPhong.setName(updatedData.getName());
            existingPhong.setPrice(updatedData.getPrice());
            existingPhong.setImageUrl(updatedData.getImageUrl());
            existingPhong.setMaTrangThai(updatedData.getMaTrangThai());
            existingPhong.setDetail(updatedData.getDetail());
            existingPhong.setGhiChu(updatedData.getGhiChu());

            Phong savedPhong = phongRepository.saveAndFlush(existingPhong);

            response.put("status", "SUCCESS");
            response.put("message", "Cập nhật dữ liệu phòng qua API PUT thành công!");
            response.put("data", savedPhong);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Lỗi khi cập nhật dữ liệu: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // =========================================================================
    // 6. XÓA BỎ HOÀN TOÀN PHÒNG KHỎI CSDL (DELETE METHOD)
    // =========================================================================
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteRoom(@PathVariable("id") Integer id) {
        Map<String, String> response = new HashMap<>();
        Optional<Phong> phongOpt = phongRepository.findById(id);

        if (phongOpt.isEmpty()) {
            response.put("message", "Mã phòng cần xóa không tồn tại trên hệ thống!");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        try {
            phongRepository.deleteById(id);
            phongRepository.flush(); // Đồng bộ dọn sạch cache CSDL lập tức

            response.put("status", "SUCCESS");
            response.put("message", "Thực thi phương thức DELETE thành công vĩnh viễn!");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Không thể xóa phòng vật lý do mã phòng đã phát sinh lịch sử hóa đơn lưu trú!");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
    }
}