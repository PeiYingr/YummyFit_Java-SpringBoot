package com.peiyingr.yummyfit.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.peiyingr.yummyfit.dto.mealRecord.MealRecordDTO;
import com.peiyingr.yummyfit.dto.user.UserDTO;
import com.peiyingr.yummyfit.entity.MealRecord;
import com.peiyingr.yummyfit.entity.User;
import com.peiyingr.yummyfit.repository.MealRecordRepository;

@Service
public class MealRecordService {
    @Autowired
    MealRecordRepository mealRecordRepository;

    public List<MealRecordDTO> searchMealRecord(UserDTO userDTO, String date, String meal) {
        User userId = new User();
        BeanUtils.copyProperties(userDTO, userId);
        List<MealRecord> mealRecordList = mealRecordRepository.searchMealRecordIdByUserIdDateMeal(userId, date, meal);
        List<MealRecordDTO> mealRecordDTOList = new ArrayList<>();

        for (MealRecord mealRecord : mealRecordList) {
            MealRecordDTO mealRecordDTO = new MealRecordDTO();
            BeanUtils.copyProperties(mealRecord,mealRecordDTO);
            mealRecordDTOList.add(mealRecordDTO);
        }
        return mealRecordDTOList;
    }
}
