package com.peiyingr.yummyfit.dto.photo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class MealPhotoDTO {
    private Integer mealPhotoId;
    private Integer mealRecordId;
    private String photo;
}
