package com.peiyingr.yummyfit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.peiyingr.yummyfit.entity.User;


public interface UserRepository extends JpaRepository<User, Long> {
    User getUserByEmail(String email);
    User getUserByUserId(Integer userId);

}