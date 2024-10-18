package com.peiyingr.yummyfit.dto.food;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class NewFoodDTO {
    private String name;
    private Float carbs;
    private Float protein;
    private Float fat;
}
