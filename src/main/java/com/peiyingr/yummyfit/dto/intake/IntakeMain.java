package com.peiyingr.yummyfit.dto.intake;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class IntakeMain {
    private Integer userId;
    private String date;
    private Integer intakeId;
    private String foodName;
    private Float amount;
    private Float carbs;
    private Float kcal;
    private Float fat;
    private Float protein;

    public IntakeMain(Integer userId, String date, Integer intakeId, String foodName, Float amount, Float carbs, Float kcal, Float fat, Float protein) {
        this.userId = userId;
        this.date = date;
        this.intakeId = intakeId;
        this.foodName = foodName;
        this.amount = amount;
        this.carbs = carbs;
        this.kcal = kcal;
        this.fat = fat;
        this.protein = protein;
    }

    public IntakeMain() {
    }
}
