package com.peiyingr.yummyfit.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.peiyingr.yummyfit.Response.YummyFitResponse;
import com.peiyingr.yummyfit.dto.food.DeleteFoodDTO;
import com.peiyingr.yummyfit.dto.food.FoodDTO;
import com.peiyingr.yummyfit.dto.food.NewFoodDTO;
import com.peiyingr.yummyfit.dto.user.UserDTO;
import com.peiyingr.yummyfit.entity.Food;
import com.peiyingr.yummyfit.entity.User;
import com.peiyingr.yummyfit.repository.FoodRepository;

@Service
public class FoodService {
    @Autowired
    public FoodRepository foodRepository;

    public List<FoodDTO> getUserFood (UserDTO userDTO) {
        User userId = new User();
        BeanUtils.copyProperties(userDTO, userId);
        List<Food> foodList = foodRepository.findFoodByUserId(userId);
        List<FoodDTO> foodDTOList = new ArrayList<>();

        // 將每個 Food 物件轉換為 FoodDTO
        for (Food food : foodList) {
            FoodDTO foodDTO = new FoodDTO();
            BeanUtils.copyProperties(food, foodDTO);
            foodDTO.setUserId(userDTO.getUserId());
            foodDTOList.add(foodDTO);
        }
        return foodDTOList;
    }

    public String findFoodByFoodId (String foodId) {
        return foodRepository.findFoodByFoodId(foodId);
    }

    public YummyFitResponse storeFood (UserDTO userDTO, NewFoodDTO newFoodDTO) {
        User userId = new User();
        BeanUtils.copyProperties(userDTO, userId);
        YummyFitResponse response = new YummyFitResponse();

        if (newFoodDTO.getName() == null || newFoodDTO.getProtein() == null || newFoodDTO.getFat() == null || newFoodDTO.getCarbs() == null) {
            response.setError(true);
            response.setMessage("Enter new food information incompletely");
            return response;
        }

        if (newFoodDTO.getProtein().isNaN() || newFoodDTO.getFat().isNaN() || newFoodDTO.getCarbs().isNaN() ||
            newFoodDTO.getProtein() < 0 || newFoodDTO.getFat() < 0 || newFoodDTO.getCarbs() < 0) {
            response.setError(true);
            response.setMessage("Please enter correct amount");
            return response;
        }

        List<Food> existingFood = searchIfFoodExist(userId, newFoodDTO.getName());
        if (!existingFood.isEmpty()) {
            response.setError(true);
            response.setMessage("Already have the same food name");
            return response;
        }

        Food food = new Food();
        food.setName(newFoodDTO.getName());
        food.setCarbs(newFoodDTO.getCarbs());
        food.setProtein(newFoodDTO.getProtein());
        food.setFat(newFoodDTO.getFat());
        food.setKcal(newFoodDTO.getCarbs() * 4 + newFoodDTO.getProtein() * 4 + newFoodDTO.getFat() * 9);
        food.setUserId(userId);

        foodRepository.save(food);
        response.setOk(true);
        return response;
    }

    public List<FoodDTO> searchFood(UserDTO userDTO, String foodName){
        User userId = new User();
        BeanUtils.copyProperties(userDTO, userId);
        List<Food> foodList = foodRepository.searchFood(userId, foodName);
        List<FoodDTO> foodDTOList = new ArrayList<>();

        for (Food food : foodList) {
            FoodDTO foodDTO = new FoodDTO();
            BeanUtils.copyProperties(food, foodDTO);
            foodDTOList.add(foodDTO);
        }
        return foodDTOList;
    }
    public List<Food> searchIfFoodExist(User userId, String foodName){
        return foodRepository.searchIfFoodExist(userId, foodName); 
    }

    public DeleteFoodDTO deleteFood(UserDTO userDTO, String foodID) {
        User userId = new User();
        BeanUtils.copyProperties(userDTO, userId);
        String foodName = findFoodByFoodId(foodID);

        foodRepository.deleteFood(userId, foodName);

        DeleteFoodDTO deleteFoodDTO = new DeleteFoodDTO();
        deleteFoodDTO.setFoodId(foodID);
        deleteFoodDTO.setName(foodName);
        return deleteFoodDTO;
    }
}
