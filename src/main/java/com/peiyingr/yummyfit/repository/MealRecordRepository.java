package com.peiyingr.yummyfit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.peiyingr.yummyfit.entity.MealRecord;
import com.peiyingr.yummyfit.entity.User;


public interface MealRecordRepository extends JpaRepository<MealRecord, Long> {
    
    //"SELECT mealRecordID FROM mealRecord WHERE userID = ? AND date = ? AND meal = ?
    @Query("SELECT mr FROM MealRecord mr WHERE mr.userId = :userId AND mr.date = :date AND mr.meal = :meal")
    List<MealRecord> searchMealRecordIdByUserIdDateMeal(User userId, String date, String meal);

    @Query("SELECT mr FROM MealRecord mr WHERE mr.userId = :userId AND mr.date = :date")
    List<MealRecord> searchDailyRecord(User userId, String date);
}