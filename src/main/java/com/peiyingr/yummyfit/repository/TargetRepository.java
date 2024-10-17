package com.peiyingr.yummyfit.repository;

import com.peiyingr.yummyfit.entity.User;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import jakarta.transaction.Transactional;

public interface TargetRepository extends JpaRepository<User, Long>{

    @Query("SELECT u FROM User u WHERE u.userId = :userId")
    User findUserTargetsByUserId(Integer userId);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.targetKcal = :targetKcal, u.targetProtein = :targetProtein, u.targetFat = :targetFat, u.targetCarbs = :targetCarbs WHERE u.userId = :userId")
    void updateUserTargets(float targetKcal, float targetProtein, float targetFat, float targetCarbs, Integer userId);
}
