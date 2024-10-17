package com.peiyingr.yummyfit.dto.user;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class UserLoginDTO {
    private String userId;
    private String name;
    private String email;
    private String password;
}
