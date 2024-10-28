package com.peiyingr.yummyfit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.peiyingr.yummyfit.entity.Food;
import com.peiyingr.yummyfit.entity.User;

import jakarta.transaction.Transactional;

import java.util.List;



public interface FoodRepository extends JpaRepository<Food, Long> {
    List<Food> findFoodByUserId(User userId);
    
    // 模糊搜尋，使用 Containing
    List<Food> findByNameContaining(String name);
    // 模糊搜尋 @Query
    @Query("SELECT f FROM Food f WHERE (f.userId = :userId OR f.userId IS NULL) AND f.name LIKE %:name%")
    List<Food> searchFood(User userId, String name);

    // "SELECT name FROM food WHERE (userID IS NULL OR userID = ?) AND name = ?", foodData
    @Query("SELECT f FROM Food f WHERE (f.userId = :userId OR f.userId IS NULL) AND f.name = :foodName")
    List<Food> searchIfFoodExist(User userId, String foodName);

    @Query("SELECT f.name FROM Food f WHERE f.foodId = :foodId")
    String findFoodByFoodId(String foodId);

    @Query("SELECT f FROM Food f WHERE f.foodId = :foodId")
    Food findFoodAllByFoodId(String foodId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Food f WHERE f.userId = :userId AND f.name = :foodName")
    void deleteFood(User userId, String foodName);
}