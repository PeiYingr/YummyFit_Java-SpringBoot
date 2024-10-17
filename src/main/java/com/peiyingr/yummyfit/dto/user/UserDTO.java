package com.peiyingr.yummyfit.dto.user;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class UserDTO {
    private Integer userId; 
    private String name; 
    private String email; 
    private String password; 
    private String avatar; 
    private String targetKcal;
    private Integer targetProtein;
    private Integer targetFat;
    private Integer targetCarbs;
}