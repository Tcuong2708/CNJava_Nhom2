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
@RequestMapping("/admin/api/rooms") // Giữ nguyên đường dẫn gốc theo ý bạn
public class AdminRoomApiController extends BaseController {

    @Autowired
    private PhongRepository phongRepository;

    // 1. TRẢ VỀ GIAO DIỆN KIỂM THỬ TRỰC TIẾP TRÊN TRÌNH DUYỆT (HTML)
    @GetMapping("")
    public String index(Model model) {
        setPageTitle(model, "API Room Playground");
        setExtraCSS(model, "view/Admin/RoomApi/index :: extra_css");
        return render(model, "view/Admin/RoomApi/index");
    }

    // 2. BẤM XEM MỚI TRẢ VỀ JSON NGUYÊN BẢN
    @GetMapping("/json")
    @ResponseBody // Ép phương thức này trả về dữ liệu thô thay vì tìm file HTML
    public ResponseEntity<List<Phong>> getAllRoomsJson() {
        List<Phong> rooms = phongRepository.findAll();
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    // 3. LẤY CHI TIẾT MỘT PHÒNG QUA AJAX (GET BY ID)
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Object> getRoomById(@PathVariable("id") Integer id) {
        Optional<Phong> phongOpt = phongRepository.findById(id);
        if (phongOpt.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Không tìm thấy phòng có ID: " + id);
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(phongOpt.get(), HttpStatus.OK);
    }

    // 4. THÊM MỚI PHÒNG QUA AJAX (POST METHOD)
    @PostMapping("")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createRoom(@RequestBody Phong newPhong) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (newPhong.getMaTrangThai() == null) {
                newPhong.setMaTrangThai(1);
            }
            Phong savedPhong = phongRepository.save(newPhong);
            response.put("status", "SUCCESS");
            response.put("message", "Thêm mới phòng thông qua API thành công!");
            response.put("data", savedPhong);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Lỗi xử lý API: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // 5. CẬP NHẬT PHÒNG QUA AJAX (PUT METHOD)
    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateRoom(@PathVariable("id") Integer id, @RequestBody Phong updatedData) {
        Map<String, Object> response = new HashMap<>();
        Optional<Phong> existingOpt = phongRepository.findById(id);

        if (existingOpt.isEmpty()) {
            response.put("status", "ERROR");
            response.put("message", "Phòng yêu cầu sửa không tồn tại!");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        try {
            Phong existingPhong = existingOpt.get();
            existingPhong.setName(updatedData.getName());
            existingPhong.setPrice(updatedData.getPrice());
            existingPhong.setImageUrl(updatedData.getImageUrl());
            existingPhong.setMaTrangThai(updatedData.getMaTrangThai());

            Phong savedPhong = phongRepository.save(existingPhong);
            response.put("status", "SUCCESS");
            response.put("message", "Cập nhật dữ liệu phòng qua API thành công!");
            response.put("data", savedPhong);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Lỗi khi cập nhật dữ liệu: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    // 6. XÓA PHÒNG QUA AJAX (DELETE METHOD)
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteRoom(@PathVariable("id") Integer id) {
        Map<String, String> response = new HashMap<>();
        Optional<Phong> phongOpt = phongRepository.findById(id);

        if (phongOpt.isEmpty()) {
            response.put("message", "Phòng không tồn tại trong hệ thống!");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        try {
            phongRepository.deleteById(id);
            response.put("status", "SUCCESS");
            response.put("message", "Thực thi phương thức DELETE thành công vĩnh viễn!");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Không thể xóa phòng do đã phát sinh lịch sử hóa đơn lưu trú!");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
    }
}