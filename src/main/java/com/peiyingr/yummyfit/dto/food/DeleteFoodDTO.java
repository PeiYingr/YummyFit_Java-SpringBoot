package com.peiyingr.yummyfit.dto.food;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class DeleteFoodDTO {
    private String foodId;
    private String name;
}
