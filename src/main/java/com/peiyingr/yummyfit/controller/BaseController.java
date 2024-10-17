package com.peiyingr.yummyfit.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import com.peiyingr.yummyfit.entity.User;


@Controller
public class BaseController {

    @GetMapping("/")
    public String index(User user) {
        return "index";
    }

    @GetMapping("/login")
    public String sign(User user) {
        return "sign";
    }

    @GetMapping("/member")
    public String member(User user) {
        return "member";
    }

    @GetMapping("/socialMedia")
    public String socialMedia(User user) {
        return "socialMedia";
    }

}
