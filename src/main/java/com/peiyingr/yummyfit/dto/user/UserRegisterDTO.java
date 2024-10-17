package com.peiyingr.yummyfit.dto.user;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class UserRegisterDTO {
    private String email;
    private String name;
    private String password;
}
