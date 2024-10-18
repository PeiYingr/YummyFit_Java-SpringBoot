package com.peiyingr.yummyfit.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.peiyingr.yummyfit.Response.YummyFitResponse;
import com.peiyingr.yummyfit.dto.user.UserDTO;
import com.peiyingr.yummyfit.service.FoodService;
import com.peiyingr.yummyfit.service.UserService;
import com.peiyingr.yummyfit.dto.food.DeleteFoodDTO;
import com.peiyingr.yummyfit.dto.food.FoodDTO;
import com.peiyingr.yummyfit.dto.food.NewFoodDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;




@RestController
@RequestMapping("/api/food")
public class FoodController {
    
    @Autowired
    public FoodService foodService;

    @Autowired
    public UserService userService;

    // fuzzy match(search) of public & own food
    @GetMapping()
    public ResponseEntity<YummyFitResponse> searchfood(
        @RequestParam String keyword,
        @CookieValue(name = "token", required = false) String cookieValue
    )
    {   
        try{
            if (cookieValue != null) {
                String userId = userService.decodedJwt(cookieValue).getUserId();
                UserDTO user = userService.getUserById(Integer.parseInt(userId));
                if (keyword != ""){
                    List<FoodDTO> foodResult = foodService.searchFood(user, keyword);
                    if (!foodResult.isEmpty()) { 
                        YummyFitResponse response = new YummyFitResponse();
                        response.setData(foodResult);
                        return ResponseEntity.status(200).body(response);
                    } else {
                        YummyFitResponse response = new YummyFitResponse();
                        response.setError(true);
                        response.setMessage("No Data");
                        // Map<String, Object> response = new HashMap<>();
                        // response.put("error", true);
                        // response.put("message", "No Data");
                        return ResponseEntity.status(200).body(response); 
                    }
                } else {
                    YummyFitResponse response = new YummyFitResponse();
                    response.setError(true);
                    response.setMessage("Enter food name");
                    return ResponseEntity.status(400).body(response);
                }
            } else {
                YummyFitResponse response = new YummyFitResponse();
                response.setError(true);
                response.setMessage("Access Denied. Please Login");
                return ResponseEntity.status(403).body(response);
            }
        } catch(Exception e) {
            YummyFitResponse response = new YummyFitResponse();
            response.setError(true);
            response.setMessage("Server error");
            return ResponseEntity.status(500).body(response);
        }
    }
    

    //get own food data
    @GetMapping("userfood")
    public ResponseEntity<YummyFitResponse> get(
        HttpServletRequest request,
        @CookieValue(name = "token", required = false) String cookieValue
    ) 
    {
        try{
            if (cookieValue != null) {
                String userId = userService.decodedJwt(cookieValue).getUserId();
                UserDTO user = userService.getUserById(Integer.parseInt(userId));
                List<FoodDTO> foodDTOList = foodService.getUserFood(user);
                YummyFitResponse response = new YummyFitResponse();
                if (!foodDTOList.isEmpty()) { 
                    response.setData(foodDTOList);
                    return ResponseEntity.status(200).body(response);
                } else {
                    response.setData(null);
                };
                return ResponseEntity.status(200).body(response); 
            } else {
                YummyFitResponse response = new YummyFitResponse();
                response.setError(true);
                response.setMessage("Access Denied. Please Login");
                return ResponseEntity.status(403).body(response);
            }
        } catch(Exception e) {
            YummyFitResponse response = new YummyFitResponse();
            response.setError(true);
            response.setMessage("Server error");
            return ResponseEntity.status(500).body(response);
        }
    }
    
    //add new(own) food to data
    @PostMapping("userfood")
    public ResponseEntity<YummyFitResponse> storeOwnFood(
        @RequestBody @Valid NewFoodDTO newFoodDTO,
        @CookieValue(name = "token", required = false) String cookieValue
    ) 
    {
        try{
            YummyFitResponse response = new YummyFitResponse();
            if (cookieValue != null) {
                String userId = userService.decodedJwt(cookieValue).getUserId();
                UserDTO user = userService.getUserById(Integer.parseInt(userId));

                response = foodService.storeFood(user, newFoodDTO);
                return ResponseEntity.status(response.isOk() ? 200 : 400).body(response);
            } else {
                response.setError(true);
                response.setMessage("Access Denied. Please Login");
                return ResponseEntity.status(403).body(response);
            }
        } catch (Exception e) {
            YummyFitResponse response = new YummyFitResponse();
            response.setError(true);
            response.setMessage("Server error");
            return ResponseEntity.status(500).body(response);
        }
    }

    //delete own food data
    @DeleteMapping("userfood")
    public ResponseEntity<YummyFitResponse> DeleteOwnFood(
        @RequestParam String foodID,
        @CookieValue(name = "token", required = false) String cookieValue
    ) 
    {
        try{
            YummyFitResponse response = new YummyFitResponse();
            if (cookieValue != null) {
                String userId = userService.decodedJwt(cookieValue).getUserId();
                UserDTO user = userService.getUserById(Integer.parseInt(userId));
                DeleteFoodDTO deletefoodDTO = foodService.deleteFood(user, foodID);
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
