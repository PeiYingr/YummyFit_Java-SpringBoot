package com.peiyingr.yummyfit.dto.mealRecord;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class MealRecordDTO {
    private Integer mealRecordId;
    private String userId;
    private String date;
    private String meal;
}