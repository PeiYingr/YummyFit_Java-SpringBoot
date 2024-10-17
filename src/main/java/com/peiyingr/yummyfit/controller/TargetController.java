package com.peiyingr.yummyfit.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.peiyingr.yummyfit.Response.YummyFitResponse;
import com.peiyingr.yummyfit.dto.user.TargetInfoDTO;
import com.peiyingr.yummyfit.dto.user.UserDTO;
import com.peiyingr.yummyfit.service.UserService;
import com.peiyingr.yummyfit.service.TargetService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/target")
public class TargetController {

    @Autowired
    public UserService userService;

    @Autowired
    public TargetService targetService;
    //add or edit target data
    @PatchMapping()
    public ResponseEntity<YummyFitResponse> patch(
        @RequestBody TargetInfoDTO targetInfoDTO,
        @CookieValue(value = "token", required = false) String cookieValue    
    ) {
        try{
            YummyFitResponse response = new YummyFitResponse();
            if (cookieValue != null) {
                String userId = userService.decodedJwt(cookieValue).getUserId();
                UserDTO user = userService.getUserById(Integer.parseInt(userId));

                response = targetService.updateTarget(user, targetInfoDTO);
                return ResponseEntity.status(response.isOk() ? 200 : 400).body(response);
            } else {
                response.setError(true);
                response.setMessage("Access Denied. Please Login");
                return ResponseEntity.status(403).body(response);
            }
        } catch(Exception e) {
            YummyFitResponse response = new YummyFitResponse();
            response.setError(true);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping()
    public ResponseEntity<YummyFitResponse> get(
        @CookieValue(value = "token", required = false) String cookieValue    
    ) {
        try{
            YummyFitResponse response = new YummyFitResponse();
            if (cookieValue != null) {
                String userId = userService.decodedJwt(cookieValue).getUserId();
                UserDTO user = userService.getUserById(Integer.parseInt(userId));
                response = targetService.getTarget(user);
                return ResponseEntity.status(response.isOk() ? 200 : 400).body(response);
            } else {
                response.setError(true);
                response.setMessage("Access Denied. Please Login");
                return ResponseEntity.status(403).body(response);
            }
        } catch(Exception e) {
            YummyFitResponse response = new YummyFitResponse();
            response.setError(true);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
