package com.peiyingr.yummyfit.dto.intake;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class DailyIntakeDTO {
    private Float totalProtein;
    private Float totalFat;
    private Float totalCarbs;
    private Float totalKcal;
    private Float proteinPercentage;
    private Float fatPercentage;
    private Float carbsPercentage;
}
