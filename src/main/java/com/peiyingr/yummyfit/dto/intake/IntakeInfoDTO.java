package com.peiyingr.yummyfit.dto.intake;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class IntakeInfoDTO {
    private String foodName;
    private Float amount;
    private String date;
    private String meal;
}
