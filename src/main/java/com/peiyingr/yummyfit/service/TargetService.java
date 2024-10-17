package com.peiyingr.yummyfit.service;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.peiyingr.yummyfit.Response.YummyFitResponse;
import com.peiyingr.yummyfit.dto.user.TargetDTO;
import com.peiyingr.yummyfit.dto.user.TargetInfoDTO;
import com.peiyingr.yummyfit.dto.user.UserDTO;
import com.peiyingr.yummyfit.entity.User;
import com.peiyingr.yummyfit.repository.TargetRepository;

@Service
public class TargetService {
    @Autowired
    TargetRepository targetRepository;

    public YummyFitResponse getTarget(UserDTO userDTO) {
        Integer userId = userDTO.getUserId();
        YummyFitResponse response = new YummyFitResponse();
        User userInfo = targetRepository.findUserTargetsByUserId(userId);

        if (userInfo.getTargetKcal() != null || userInfo.getTargetProtein() != null || userInfo.getTargetCarbs() != null || userInfo.getTargetFat() != null) {
            int targetKcal = userInfo.getTargetKcal();
            int targetProtein = userInfo.getTargetProtein();
            int targetCarbs = userInfo.getTargetCarbs();
            int targetFat = userInfo.getTargetFat();
            float proteinAmount = Math.round((targetKcal * (float) targetProtein / 100 / 4 + Float.MIN_VALUE) * 100) / 100.0f;
            float fatAmount = Math.round((targetKcal * (float) targetFat / 100 / 9 + Float.MIN_VALUE) * 100) / 100.0f;
            float carbsAmount = Math.round((targetKcal * (float) targetCarbs / 100 / 4 + Float.MIN_VALUE) * 100) / 100.0f;
            TargetDTO targetDTO = new TargetDTO();
            targetDTO.setTargetKcal(targetKcal);
            targetDTO.setTargetProtein(targetProtein);
            targetDTO.setTargetFat(targetFat);
            targetDTO.setTargetCarbs(targetCarbs);
            targetDTO.setProteinAmount(proteinAmount);
            targetDTO.setFatAmount(fatAmount);
            targetDTO.setCarbsAmount(carbsAmount);
            response.setData(targetDTO);
            response.setOk(true);
        } else {
            response.setData(null);
            response.setOk(true);
        }
        return response;
    }


    public YummyFitResponse updateTarget(UserDTO userDTO, TargetInfoDTO targetInfoDTO) {
        Integer userId = userDTO.getUserId();
        YummyFitResponse response = new YummyFitResponse();
        if (targetInfoDTO.getTargetKcal().equals(null) || targetInfoDTO.getTargetKcal() < 0) {
            response.setError(true);
            response.setMessage("Enter correct Calories.");
            return response;
        }

        if (targetInfoDTO.getTargetProtein().equals(null) || targetInfoDTO.getTargetProtein() < 0) {
            response.setError(true);
            response.setMessage("Enter an integer percentage of protein.");
            return response;
        }

        if (targetInfoDTO.getTargetFat().equals(null) || targetInfoDTO.getTargetFat() < 0) {
            response.setError(true);
            response.setMessage("Enter an integer percentage of fat.");
            return response;
        }

        if (targetInfoDTO.getTargetCarbs().equals(null) || targetInfoDTO.getTargetCarbs() < 0) {
            response.setError(true);
            response.setMessage("Enter an integer percentage of carbs.");
            return response;
        }
        int targetKcal = targetInfoDTO.getTargetKcal();
        int targetProtein = targetInfoDTO.getTargetProtein();
        int targetCarbs = targetInfoDTO.getTargetCarbs();
        int targetFat = targetInfoDTO.getTargetFat();
        if (targetProtein + targetFat + targetCarbs != 100) {
            response.setError(true);
            response.setMessage("Percentages not adding up to 100%.");
            return response;
        }
        targetRepository.updateUserTargets(targetKcal, targetProtein, targetFat, targetCarbs, userId);

        float proteinAmount = Math.round((targetKcal * (float) targetProtein / 100 / 4 + Float.MIN_VALUE) * 100) / 100.0f;
        float fatAmount = Math.round((targetKcal * (float) targetFat / 100 / 9 + Float.MIN_VALUE) * 100) / 100.0f;
        float carbsAmount = Math.round((targetKcal * (float) targetCarbs / 100 / 4 + Float.MIN_VALUE) * 100) / 100.0f;

        TargetDTO targetDTO = new TargetDTO();
        targetDTO.setTargetKcal(targetKcal);
        targetDTO.setTargetProtein(targetProtein);
        targetDTO.setTargetFat(targetFat);
        targetDTO.setTargetCarbs(targetCarbs);
        targetDTO.setProteinAmount(proteinAmount);
        targetDTO.setFatAmount(fatAmount);
        targetDTO.setCarbsAmount(carbsAmount);

        response.setData(targetDTO);
        response.setOk(true);
        return response;
    }

}
