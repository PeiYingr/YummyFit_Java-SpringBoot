package com.peiyingr.yummyfit.controller;

import org.springframework.web.bind.annotation.RestController;

import com.peiyingr.yummyfit.service.IntakeService;
import com.peiyingr.yummyfit.service.MealRecordService;
import com.peiyingr.yummyfit.service.UserService;

import com.peiyingr.yummyfit.Response.YummyFitResponse;
import com.peiyingr.yummyfit.dto.intake.DailyIntakeDTO;
import com.peiyingr.yummyfit.dto.intake.DailyWeeklyIntakeDTO;
import com.peiyingr.yummyfit.dto.intake.WeeklyIntakeDTO;
import com.peiyingr.yummyfit.dto.user.UserDTO;
import com.peiyingr.yummyfit.dto.mealRecord.MealRecordDTO;
import com.peiyingr.yummyfit.dto.intake.IntakeInfoDTO;
import com.peiyingr.yummyfit.dto.intake.IntakeMain;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;




@RestController
@RequestMapping("/api/intake")
public class IntakeController {
    
    @Autowired
    MealRecordService mealRecordService;

    @Autowired
    UserService userService;

    @Autowired
    IntakeService intakeService;

    // get meal intake data
    @GetMapping()
    public ResponseEntity<YummyFitResponse> get(
        @RequestParam String meal,
        @RequestParam String date,
        @CookieValue(value = "token", required = false) String cookieValue
    ) 
    {
        try{
            YummyFitResponse response = new YummyFitResponse();
            if (cookieValue != null) {
                String userId = userService.decodedJwt(cookieValue).getUserId();
                UserDTO user = userService.getUserById(Integer.parseInt(userId));
                List<MealRecordDTO> mealRecord = mealRecordService.searchMealRecord(user, date, meal);
                if (mealRecord != null) {
                    List<IntakeMain> mealIntake  = intakeService.searchMealIntake(mealRecord);
                    response.setData(mealIntake);
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
    
    // add food intake data
    @PostMapping()
    public ResponseEntity<YummyFitResponse> post(
        @RequestBody IntakeInfoDTO intakeInfoDTO,
        @CookieValue(value = "token", required = false) String cookieValue    
    ) {
        try{
            YummyFitResponse response = new YummyFitResponse();
            if (cookieValue != null) {
                String userId = userService.decodedJwt(cookieValue).getUserId();
                UserDTO user = userService.getUserById(Integer.parseInt(userId));

                response = intakeService.store(user, intakeInfoDTO);
                return ResponseEntity.status(response.isOk() ? 200 : 400).body(response);
                // JsonArray jsonArray = gson.toJsonTree(intakeMains).getAsJsonArray();
                // JsonObject dataWrapper = new JsonObject();
                // dataWrapper.add("data", jsonArray);
                // String jsonResult = gson.toJson(dataWrapper);
                // return ResponseEntity.status(200).body(jsonResult);
            } else {
                response.setError(true);
                response.setMessage("Access Denied. Please Login");
                return ResponseEntity.status(403).body(response);
            }
        } catch(Exception e) {
            YummyFitResponse response = new YummyFitResponse();
            response.setError(true);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    // delete food intake data
    @DeleteMapping("")
    public ResponseEntity<YummyFitResponse> DeleteOwnFood(
        @RequestParam String intakeID,
        @CookieValue(name = "token", required = false) String cookieValue
    ) 
    {
        try{
            YummyFitResponse response = new YummyFitResponse();
            if (cookieValue != null) {
                String userId = userService.decodedJwt(cookieValue).getUserId();
                UserDTO user = userService.getUserById(Integer.parseInt(userId));
                UserDTO intakeUser = intakeService.findUserByIntakeId(intakeID);
                if (user.getUserId().equals(intakeUser.getUserId())) {
                    String deleteIntakeId = intakeService.deleteIntakeFood(intakeID);
                    response.setOk(true);
                    return ResponseEntity.status(200).body(response);
                } else {
                    response.setError(true);
                    response.setMessage("The intake record does not exist or does not belong to this member");
                    return ResponseEntity.status(400).body(response);
                }
            } else {
                response.setError(true);
                response.setMessage("Access Denied. Please Login");
                return ResponseEntity.status(403).body(response);
            }
        } catch(Exception e) {;
            YummyFitResponse response = new YummyFitResponse();
            response.setError(true);
            response.setMessage("Server error");
            System.out.println(e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

        // get daily & week intake data
        @GetMapping("daily")
        public ResponseEntity<YummyFitResponse> get(
            @RequestParam String date,
            @CookieValue(value = "token", required = false) String cookieValue
        ) 
        {
            try{
                YummyFitResponse response = new YummyFitResponse();
                if (cookieValue != null) {
                    String userId = userService.decodedJwt(cookieValue).getUserId();
                    UserDTO user = userService.getUserById(Integer.parseInt(userId));
                    DailyIntakeDTO dailyIntakeDTO = intakeService.calculateDailyIntake(user, date);
                    List<String> dates = intakeService.findWeeklyDate(date);
                    WeeklyIntakeDTO weeklyIntakeDTO = intakeService.calculateWeeklyIntake(user,dates);
                    DailyWeeklyIntakeDTO dailyWeeklyIntakeDTO = new DailyWeeklyIntakeDTO();
                    dailyWeeklyIntakeDTO.setDaily(dailyIntakeDTO);
                    dailyWeeklyIntakeDTO.setWeek(weeklyIntakeDTO);
                    response.setData(dailyWeeklyIntakeDTO);
                    return ResponseEntity.status(200).body(response);
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
