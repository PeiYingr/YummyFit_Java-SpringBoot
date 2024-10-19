package com.peiyingr.yummyfit.dto.intake;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Data
public class WeeklyIntakeDTO {
    private List<String> weekDates;
    private List<Float> weekKcal;
    private List<Float> weekProteinPercentage;
    private List<Float> weekFatPercentage;
    private List<Float> weekCarbsPercentage;
}
