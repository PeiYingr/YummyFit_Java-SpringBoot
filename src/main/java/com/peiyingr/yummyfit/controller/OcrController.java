package com.peiyingr.yummyfit.controller;

import com.peiyingr.yummyfit.Response.YummyFitResponse;
import com.peiyingr.yummyfit.dto.user.UserDTO;
import com.peiyingr.yummyfit.service.OcrService;
import com.peiyingr.yummyfit.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/ocr")
public class OcrController {
    @Autowired
    public UserService userService;

    @Autowired
    public OcrService ocrService;

    @PostMapping("")
    public ResponseEntity<YummyFitResponse> uploadImage(
        @RequestPart(value = "image", required = false) MultipartFile imageFile,
        @CookieValue(value = "token", required = false) String cookieValue
    ) {
        try {
            YummyFitResponse response = new YummyFitResponse();
            if (cookieValue != null) {
                String userId = userService.decodedJwt(cookieValue).getUserId();
                UserDTO user = userService.getUserById(Integer.parseInt(userId));
                if (imageFile == null || imageFile.isEmpty())  {
                    response.setError(true);
                    response.setMessage("No file selected");
                    return ResponseEntity.status(400).body(response);
                }
                
                response = ocrService.detectText(imageFile);
                return ResponseEntity.status(response.isOk() ? 200 : 400).body(response);

            } else {
                response.setError(true);
                response.setMessage("Access Denied. Please Login");
                return ResponseEntity.status(403).body(response);
            }
        } catch(Exception e) {
            YummyFitResponse response = new YummyFitResponse();
            response.setError(true);
            response.setMessage(e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
