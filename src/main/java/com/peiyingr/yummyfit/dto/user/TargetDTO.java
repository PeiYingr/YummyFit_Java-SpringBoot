package com.peiyingr.yummyfit.dto.user;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class TargetDTO {
    private Integer targetKcal;
    private Integer targetProtein;
    private Integer targetFat;
    private Integer targetCarbs;
    private Float proteinAmount;
    private Float fatAmount;
    private Float carbsAmount;
}
