package com.peiyingr.yummyfit.dto.intake;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class DailyWeeklyIntakeDTO {
    private DailyIntakeDTO daily;
    private WeeklyIntakeDTO week;
}
