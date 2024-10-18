package com.peiyingr.yummyfit.dto.food;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class NutritionFactDTO {
    private String name;
    private String proValue;
    private String fatValue;
    private String carbsValue;
}
