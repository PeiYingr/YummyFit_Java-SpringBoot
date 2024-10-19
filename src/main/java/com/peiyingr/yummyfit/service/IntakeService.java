package com.peiyingr.yummyfit.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.peiyingr.yummyfit.Response.YummyFitResponse;
import com.peiyingr.yummyfit.dto.intake.DailyIntakeDTO;
import com.peiyingr.yummyfit.dto.intake.IntakeInfoDTO;
import com.peiyingr.yummyfit.dto.intake.IntakeMain;
import com.peiyingr.yummyfit.dto.intake.WeeklyIntakeDTO;
import com.peiyingr.yummyfit.dto.mealRecord.MealRecordDTO;
import com.peiyingr.yummyfit.dto.user.UserDTO;
import com.peiyingr.yummyfit.entity.Food;
import com.peiyingr.yummyfit.entity.Intake;
import com.peiyingr.yummyfit.entity.MealRecord;
import com.peiyingr.yummyfit.entity.User;
import com.peiyingr.yummyfit.repository.FoodRepository;
import com.peiyingr.yummyfit.repository.IntakeRepository;
import com.peiyingr.yummyfit.repository.MealRecordRepository;

@Service
public class IntakeService {
    
    @Autowired
    IntakeRepository intakeRepository;

    @Autowired
    FoodRepository foodRepository;

    @Autowired
    MealRecordRepository mealRecordRepository;

    public UserDTO findUserByIntakeId (String intakeId) {
        User user = intakeRepository.findUserByIntakeId(intakeId);
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }

    public List<IntakeMain> searchMealIntake(List<MealRecordDTO> mealRecordDTOList) {
        List<MealRecord> mealRecordList = new ArrayList<>();
        for (MealRecordDTO mealRecordDTO : mealRecordDTOList) {
            MealRecord mealRecord = new MealRecord();
            BeanUtils.copyProperties(mealRecordDTO, mealRecord);
            mealRecordList.add(mealRecord);
        }
        List<IntakeMain> mealIntake = intakeRepository.searchMealIntake(mealRecordList);
        if (!mealIntake.isEmpty()) {;
            List<IntakeMain> allMealData = responseMealData(mealIntake);
            return allMealData;
        } else {
            return null;
        }
    }

    public YummyFitResponse store(UserDTO userDTO, IntakeInfoDTO intakeInfoDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO, user);
        YummyFitResponse response = new YummyFitResponse();
        if (intakeInfoDTO.getFoodName() == null || intakeInfoDTO.getAmount() == null) {
            response.setError(true);
            response.setMessage("Enter name and amount of food");
            return response;
        } else if (intakeInfoDTO.getAmount() <= 0 || intakeInfoDTO.getAmount().isNaN()) {
            response.setError(true);
            response.setMessage("Please enter correct amount of food");
            return response;
        } else {
            List<Food> foodResult = foodRepository.searchIfFoodExist(user, intakeInfoDTO.getFoodName());
            if(!foodResult.isEmpty()) {
                List<MealRecord> mealRecordIDSearch = mealRecordRepository.searchMealRecordIdByUserIdDateMeal(user, intakeInfoDTO.getDate(), intakeInfoDTO.getMeal());
                if(mealRecordIDSearch.isEmpty()) {
                    MealRecord mealRecord = new MealRecord();
                    mealRecord.setDate(intakeInfoDTO.getDate());
                    mealRecord.setMeal(intakeInfoDTO.getMeal());
                    mealRecord.setUserId(user);
                    mealRecordRepository.save(mealRecord);
                    mealRecordIDSearch = mealRecordRepository.searchMealRecordIdByUserIdDateMeal(user, intakeInfoDTO.getDate(), intakeInfoDTO.getMeal());
                }
                MealRecord mealRecord = mealRecordIDSearch.get(0);
                Intake intake = new Intake();
                intake.setMealRecordId(mealRecord);
                intake.setAmount(intakeInfoDTO.getAmount());
                intake.setFoodId(foodResult.get(0));
                intakeRepository.save(intake);
                List<IntakeMain> mealIntake = intakeRepository.searchMealIntake(mealRecordIDSearch);
                if (!mealIntake.isEmpty()) {;
                    List<IntakeMain> allMealData = responseMealData(mealIntake);
                    response.setData(allMealData);
                    response.setOk(true);
                } else {
                    response.setData(null);
                    response.setOk(true);
                }
            } else {
                response.setError(true);
                response.setMessage("Food data not exist");
            }
            return response;        
        }
    }

    public List<IntakeMain> responseMealData(List<IntakeMain> result) {
        List<IntakeMain> allMealData = new ArrayList<>();
        for (IntakeMain intake : result) {
            float protein = Math.round(((intake.getAmount() / 100.0f) * intake.getProtein() + Float.MIN_VALUE) * 100.0f) / 100.0f;
            float fat = Math.round(((intake.getAmount() / 100.0f) * intake.getFat() + Float.MIN_VALUE) * 100.0f) / 100.0f;
            float carbs = Math.round(((intake.getAmount() / 100.0f) * intake.getCarbs() + Float.MIN_VALUE) * 100.0f) / 100.0f;
            float kcal = Math.round(((intake.getAmount() / 100.0f) * intake.getKcal() + Float.MIN_VALUE) * 10.0f) / 10.0f;
    
            IntakeMain intakeMain = new IntakeMain();
            intakeMain.setUserId(intake.getUserId());
            intakeMain.setDate(intake.getDate());
            intakeMain.setIntakeId(intake.getIntakeId());
            intakeMain.setFoodName(intake.getFoodName());
            intakeMain.setAmount(intake.getAmount());
            intakeMain.setProtein(protein);
            intakeMain.setFat(fat);
            intakeMain.setCarbs(carbs);
            intakeMain.setKcal(kcal);
    
            allMealData.add(intakeMain);
        }

        return allMealData;
    }

    public String deleteIntakeFood(String intakeId) {
        intakeRepository.deleteIntakeFood(intakeId);
        return intakeId;
    }

    public List<String> findWeeklyDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate intakeDate = LocalDate.parse(date, formatter);
        List<String> dates = new ArrayList<>();
        
        for (int i = 0; i < 7; i++) {
            LocalDate currentDate = intakeDate.minusDays(i);
            String formattedDate = currentDate.format(formatter);
            dates.add(formattedDate);
        }
        // System.out.println("5: " +dates);
        Collections.reverse(dates); // 將列表翻轉
        // System.out.println("6: " +dates);
        return dates;
    }

    public DailyIntakeDTO calculateDailyIntake(UserDTO userDTO, String date) {
        User userId = new User();
        BeanUtils.copyProperties(userDTO, userId);
        List<MealRecord> dailyMealRecordIds = mealRecordRepository.searchDailyRecord(userId, date);
        if (!dailyMealRecordIds.isEmpty()) {
            List<IntakeMain> intakeList = intakeRepository.searchMealIntake(dailyMealRecordIds);
            if (!intakeList.isEmpty()) {
                float totalKcal = 0;
                float totalProtein = 0;
                float totalFat = 0;
                float totalCarbs = 0;

                for (IntakeMain intake : intakeList) {
                    float protein = (intake.getAmount() / 100) * intake.getProtein();
                    float fat = (intake.getAmount() / 100) * intake.getFat();
                    float carbs = (intake.getAmount() / 100) * intake.getCarbs();
                    float kcal = (intake.getAmount() / 100) * intake.getKcal();

                    totalProtein += protein;
                    totalFat += fat;
                    totalCarbs += carbs;
                    totalKcal += kcal;
                }

                totalProtein = Math.round((totalProtein + 0.0001f) * 100) / 100.0f;
                totalFat = Math.round((totalFat + 0.0001f) * 100) / 100.0f;
                totalCarbs = Math.round((totalCarbs + 0.0001f) * 100) / 100.0f;
                totalKcal = Math.round(totalKcal);

                float proteinPercentage = Math.round((totalProtein * 4 / totalKcal) * 100);
                float fatPercentage = Math.round((totalFat * 9 / totalKcal) * 100);
                float carbsPercentage = 100 - proteinPercentage - fatPercentage;

                DailyIntakeDTO dailyIntakeDTO = new DailyIntakeDTO();
                dailyIntakeDTO.setTotalProtein(totalProtein);
                dailyIntakeDTO.setTotalFat(totalFat);
                dailyIntakeDTO.setTotalCarbs(totalCarbs);
                dailyIntakeDTO.setTotalKcal(totalKcal);
                dailyIntakeDTO.setProteinPercentage(proteinPercentage);
                dailyIntakeDTO.setFatPercentage(fatPercentage);
                dailyIntakeDTO.setCarbsPercentage(carbsPercentage);

                return dailyIntakeDTO;

            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public WeeklyIntakeDTO calculateWeeklyIntake(UserDTO userDTO, List<String> weekDates) {
        List<Float> weekKcal = new ArrayList<>();
        List<Float> weekProteinPercentage = new ArrayList<>();
        List<Float> weekFatPercentage = new ArrayList<>();
        List<Float> weekCarbsPercentage = new ArrayList<>();
        
        for (String weekDate : weekDates) {
            DailyIntakeDTO daily = calculateDailyIntake(userDTO, weekDate);
            
            if (daily != null) {
                weekKcal.add(daily.getTotalKcal());
                weekProteinPercentage.add(daily.getProteinPercentage());
                weekFatPercentage.add(daily.getFatPercentage());
                weekCarbsPercentage.add(daily.getCarbsPercentage());
            } else {
                weekKcal.add(0.0f);
                weekProteinPercentage.add(0.0f);
                weekFatPercentage.add(0.0f);
                weekCarbsPercentage.add(0.0f);
            }
        }

        WeeklyIntakeDTO weeklyIntakeDTO = new WeeklyIntakeDTO();
        weeklyIntakeDTO.setWeekDates(weekDates);
        weeklyIntakeDTO.setWeekKcal(weekKcal);
        weeklyIntakeDTO.setWeekProteinPercentage(weekProteinPercentage);
        weeklyIntakeDTO.setWeekFatPercentage(weekFatPercentage);
        weeklyIntakeDTO.setWeekCarbsPercentage(weekCarbsPercentage);

        return weeklyIntakeDTO;
    }
}
