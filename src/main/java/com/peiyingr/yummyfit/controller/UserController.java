package com.peiyingr.yummyfit.controller;

import com.peiyingr.yummyfit.Response.YummyFitResponse;
import com.peiyingr.yummyfit.dto.user.UserLoginDTO;
import com.peiyingr.yummyfit.dto.user.UserRegisterDTO;
import com.peiyingr.yummyfit.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired              
    public UserService userService;

    // get signin status/information
    @GetMapping()
    public ResponseEntity<YummyFitResponse> get(
        HttpServletRequest request,
        @CookieValue(value = "token", required = false) String cookieValue
    ) 
    {
        try{
            YummyFitResponse response = new YummyFitResponse();
            if (cookieValue != null) {
                UserLoginDTO userLoginDTO = this.userService.decodedJwt(cookieValue);
                response.setData(userLoginDTO);
                response.setOk(true);
                return ResponseEntity.status(200).body(response);

            } else {
                response.setError(true);
                response.setMessage("Access Denied. Please Login");
                return ResponseEntity.status(403).body(response);
            }
        } catch (Exception e) {
            YummyFitResponse response = new YummyFitResponse();
            response.setError(true);
            response.setMessage("Server error");
            return ResponseEntity.status(500).body(response);
        }
    }

    // signup(register)
    @PostMapping()
    public ResponseEntity<YummyFitResponse> create(@RequestBody @Valid UserRegisterDTO userRegisterDTO) {
        try{
            YummyFitResponse response = userService.register(userRegisterDTO);
            if (response.isOk()) {
                UserLoginDTO userLoginDTO = new UserLoginDTO(); 
                userLoginDTO.setEmail(userRegisterDTO.getEmail());
                userLoginDTO.setPassword(userRegisterDTO.getPassword());

                signin(userLoginDTO);
                return ResponseEntity.status(response.isOk() ? 200 : 400).body(response);
            } else {
                return ResponseEntity.status(response.isOk() ? 200 : 400).body(response);
            }
        } catch (Exception e) {
            YummyFitResponse response = new YummyFitResponse();
            response.setError(true);
            response.setMessage("Server error");
            return ResponseEntity.status(500).body(response);
        }
    }

    // signin
    @PutMapping()
    public ResponseEntity<YummyFitResponse> signin(@RequestBody @Valid UserLoginDTO userLoginDTO) {
        try{
            YummyFitResponse response = userService.login(userLoginDTO);
            if (response.isOk()) {
                UserLoginDTO user = (UserLoginDTO) response.getData();
                String token =this.userService.setJwt(user);
                Cookie cookie = new Cookie("token", "token=" + token);
                cookie.setMaxAge(36000);
                cookie.setPath("/");
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.SET_COOKIE, cookie.getValue());
                return ResponseEntity.status(200).headers(headers).body(response);
            } else {
                return ResponseEntity.status(response.isOk() ? 200 : 400).body(response);
            }
        } catch (Exception e) {
            YummyFitResponse response = new YummyFitResponse();
            response.setError(true);
            response.setMessage("Server error");
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @DeleteMapping()
    public ResponseEntity<YummyFitResponse> signout(
        HttpServletResponse httpResponse,
        @CookieValue(value = "token", required = false) Cookie cookieValue
    ) {
        try {
            YummyFitResponse response = new YummyFitResponse();
            if (cookieValue != null) {
                cookieValue.setMaxAge(0);  // 設置 MaxAge 為 0 以刪除 cookie
                response.setOk(true);
                cookieValue.setMaxAge(0);
                httpResponse.addCookie(cookieValue);
                return ResponseEntity.status(200).body(response);
            } else {
                response.setError(true);
                response.setMessage("Access Denied. Please Login");
                return ResponseEntity.status(403).body(response);
            }
        } catch (Exception e) {
            YummyFitResponse response = new YummyFitResponse();
            response.setError(true);
            response.setMessage("Server error");
            return ResponseEntity.status(500).body(response);
        }
    } 
}
