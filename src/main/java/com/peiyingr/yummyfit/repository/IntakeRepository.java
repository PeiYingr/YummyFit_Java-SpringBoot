package com.peiyingr.yummyfit.repository;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.peiyingr.yummyfit.dto.intake.IntakeMain;
import com.peiyingr.yummyfit.entity.Intake;
import com.peiyingr.yummyfit.entity.MealRecord;
import com.peiyingr.yummyfit.entity.User;

import jakarta.transaction.Transactional;

public interface IntakeRepository extends JpaRepository<Intake, Long> {

    
    @Query("SELECT new com.peiyingr.yummyfit.dto.intake.IntakeMain(m.userId.userId, m.date, i.intakeId, f.name, i.amount, f.carbs, f.kcal, f.fat, f.protein) " +
    "FROM Intake i JOIN i.foodId f JOIN i.mealRecordId m WHERE i.mealRecordId IN (:mealRecordId)")
    List<IntakeMain> searchMealIntake(List<MealRecord> mealRecordId);

    @Query("SELECT mr.userId FROM MealRecord mr JOIN mr.intakes i WHERE i.intakeId = :intakeId")
    User findUserByIntakeId(String intakeId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Intake i WHERE i.intakeId = :intakeId")
    void deleteIntakeFood(String intakeId);

    // @Query("SELECT new com.peiyingr.yummyfit.dto.intake.IntakeMain(m.userId.userId, m.date, i.intakeId, f.name, i.amount, f.carbs, f.kcal, f.fat, f.protein) " +
    // "FROM Intake i JOIN i.foodId f JOIN i.mealRecordId m WHERE i.mealRecordId IN (:mealRecordIds)")
    // List<IntakeMain> searchDailyIntake(List<Integer> mealRecordIds);
}