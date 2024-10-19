package com.peiyingr.yummyfit.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;
import com.peiyingr.yummyfit.Response.YummyFitResponse;
import com.peiyingr.yummyfit.dto.mealRecord.MealRecordDTO;
import com.peiyingr.yummyfit.dto.user.UserDTO;
import com.peiyingr.yummyfit.service.UserService;
import com.peiyingr.yummyfit.service.PhotoService;
import com.peiyingr.yummyfit.service.MealRecordService;
import com.peiyingr.yummyfit.dto.photo.MealPhotoDTO;
import com.peiyingr.yummyfit.dto.photo.DeleteMealPhotoDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/api/photo")
public class PhotoController {

    @Autowired
    public UserService userService;

    @Autowired
    public PhotoService photoService;

    @Autowired
    MealRecordService mealRecordService;

    // avatar uploads(update)
    @PatchMapping("avatar")
    public ResponseEntity<YummyFitResponse> patchAvatarPhoto(
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
                response = photoService.updateAvatar(imageFile, user);
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

    // get avatar
    @GetMapping("avatar")
    public ResponseEntity<YummyFitResponse> getAvatarPhoto(
        @CookieValue(value = "token", required = false) String cookieValue
    ) {
        try {
            YummyFitResponse response = new YummyFitResponse();
            if (cookieValue != null) {
                String userId = userService.decodedJwt(cookieValue).getUserId();
                UserDTO user = userService.getUserById(Integer.parseInt(userId));
                response = photoService.getAvatar(user);
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

    // get meal photo
    @GetMapping("meal")
    public ResponseEntity<YummyFitResponse> getMealPhoto(
        @RequestParam String meal,
        @RequestParam String date,
        @CookieValue(value = "token", required = false) String cookieValue
    ) {
        try {
            YummyFitResponse response = new YummyFitResponse();
            if (cookieValue != null) {
                String userId = userService.decodedJwt(cookieValue).getUserId();
                UserDTO user = userService.getUserById(Integer.parseInt(userId));
                List<MealRecordDTO> mealRecord = mealRecordService.searchMealRecord(user, date, meal);
                if (mealRecord != null) {
                    List<MealPhotoDTO> mealPhoto = photoService.getMealPhoto(mealRecord);
                    response.setData(mealPhoto);
                    response.setOk(true);
                } else {
                    response.setData(null);
                    response.setOk(true);
                }
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

    // meal photo uploads
    @PostMapping("meal")
    public ResponseEntity<YummyFitResponse> patchAvatarPhoto(
        @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles,
        @RequestParam String whichMeal,
        @RequestParam String date,
        @CookieValue(value = "token", required = false) String cookieValue
    ) {
        try {
            YummyFitResponse response = new YummyFitResponse();
            if (cookieValue != null) {
                String userId = userService.decodedJwt(cookieValue).getUserId();
                UserDTO user = userService.getUserById(Integer.parseInt(userId));
                if (imageFiles == null || imageFiles.isEmpty())  {
                    response.setError(true);
                    response.setMessage("No file selected");
                    return ResponseEntity.status(400).body(response);
                }
                List<MealPhotoDTO> mealPhotoDTOList  = photoService.storeMealPhotos(imageFiles, user, date, whichMeal);
                response.setData(mealPhotoDTOList);
                response.setOk(true);
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

    // delete meal photo
    @DeleteMapping("meal")
    public ResponseEntity<YummyFitResponse> DeleteOwnFood(
        @RequestParam String mealPhotoID,
        @CookieValue(name = "token", required = false) String cookieValue
    ) 
    {
        try{
            YummyFitResponse response = new YummyFitResponse();
            if (cookieValue != null) {
                String userId = userService.decodedJwt(cookieValue).getUserId();
                UserDTO user = userService.getUserById(Integer.parseInt(userId));
                DeleteMealPhotoDTO deleteMealPhotoDTO = photoService.deleteMealPhoto(user, mealPhotoID);
                response.setOk(true);
                return ResponseEntity.status(200).body(response);

            } else {
                response.setError(true);
                response.setMessage("Access Denied. Please Login");
                return ResponseEntity.status(403).body(response);
            }
        } catch(Exception e) {;
            YummyFitResponse response = new YummyFitResponse();
            response.setError(true);
            response.setMessage("Server error");
            return ResponseEntity.status(500).body(response);
        }
    }
}
