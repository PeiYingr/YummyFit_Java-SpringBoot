package com.peiyingr.yummyfit.repository;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.peiyingr.yummyfit.entity.MealPhoto;
import com.peiyingr.yummyfit.entity.MealRecord;

import jakarta.transaction.Transactional;

public interface MealPhotoRepository extends JpaRepository<MealPhoto, Long>{

    @Query("SELECT mp FROM MealPhoto mp WHERE mp.mealRecordId IN (:mealRecordId)")
    List<MealPhoto> getMealPhoto(List<MealRecord> mealRecordId);

    @Query("SELECT mr.userId.userId FROM MealRecord mr INNER JOIN mr.mealPhotos mp WHERE mp.mealPhotoId = :mealPhotoId")
    Integer findUserByMealPhotoId(String mealPhotoId);

    @Modifying
    @Transactional
    @Query("DELETE FROM MealPhoto mp WHERE mp.mealPhotoId= :mealPhotoId")
    void deleteMealPhoto(String mealPhotoId);
}