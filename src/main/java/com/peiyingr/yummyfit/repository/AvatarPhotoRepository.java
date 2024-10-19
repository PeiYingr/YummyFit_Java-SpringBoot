package com.peiyingr.yummyfit.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.peiyingr.yummyfit.entity.User;

import jakarta.transaction.Transactional;

public interface AvatarPhotoRepository extends JpaRepository<User, Long>{

    @Query("SELECT u FROM User u WHERE u.userId = :userId")
    User findUserAvatarByUserId(Integer userId);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.avatar = :avatarUrl WHERE u.userId = :userId")
    void updateUAvatar(String avatarUrl, Integer userId);
}