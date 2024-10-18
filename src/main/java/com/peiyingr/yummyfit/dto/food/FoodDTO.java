package com.peiyingr.yummyfit.dto.food;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class FoodDTO {
    private Integer userId;
    private Integer foodId;
    private String name; 
    private Float kcal; 
    private Float protein;
    private Float fat;
    private Float carbs;
}
