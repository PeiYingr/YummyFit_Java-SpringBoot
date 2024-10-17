package com.peiyingr.yummyfit.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.peiyingr.yummyfit.Response.YummyFitResponse;
import com.peiyingr.yummyfit.dto.food.FoodDTO;
import com.peiyingr.yummyfit.dto.user.UserDTO;
import com.peiyingr.yummyfit.dto.user.UserLoginDTO;
import com.peiyingr.yummyfit.dto.user.UserRegisterDTO;
import com.peiyingr.yummyfit.entity.User;
import com.peiyingr.yummyfit.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    public UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder  = new BCryptPasswordEncoder();

    private String SECRET_KEY = "secretKey";
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-.]+){1,}$";
    public static final String PASSWORD_REGEX = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{6,20}$";

    public YummyFitResponse register(UserRegisterDTO userRegisterDTO) {
        String email = userRegisterDTO.getEmail();
        String password = userRegisterDTO.getPassword();
        String name = userRegisterDTO.getName();
        YummyFitResponse response = new YummyFitResponse();
        if (name.equals("") || email.equals("") || password.equals("")) {
            response.setError(true);
            response.setMessage("⚠️ Enter your name, Email and password");
        } else {
            User userEmail = userRepository.getUserByEmail(email);
            if (userEmail != null) {
                response.setError(true);
                response.setMessage(String.format("⚠️Email: %s has been registered", userRegisterDTO.getEmail()));
            } else {
                if (email.matches(EMAIL_REGEX) && password.matches(PASSWORD_REGEX)) {
                    User newUser = new User();
                    newUser.setName(userRegisterDTO.getName());
                    newUser.setEmail(userRegisterDTO.getEmail());

                    String hashedPassword = passwordEncoder.encode(userRegisterDTO.getPassword());
                    newUser.setPassword(hashedPassword);
                    User user = userRepository.save(newUser);
                    response.setData(user);
                    response.setOk(true);
                } else if (!email.matches(EMAIL_REGEX)) {
                    response.setError(true);
                    response.setMessage("⚠️ Invaild Email");
                } else {
                    response.setError(true);
                    response.setMessage("⚠️ Password need to be 6-20 chars, at least one letter and one number!");
                }
            }
        }
        return response;
    }

    public YummyFitResponse login(UserLoginDTO userLoginDTO) {
        YummyFitResponse response = new YummyFitResponse();
        String email = userLoginDTO.getEmail();
        String password = userLoginDTO.getPassword();
        if (email.equals("") || password.equals("")) {
            response.setError(true);
            response.setMessage("⚠️ Enter your Email and password");
        } else {
            User user = userRepository.getUserByEmail(email);
            if (user != null) {
                //
                boolean isPasswordMatch = passwordEncoder.matches(password, user.getPassword());
                if (email.equals(user.getEmail()) && isPasswordMatch) {
                    UserLoginDTO userLoginData = new UserLoginDTO();
                    userLoginData.setUserId(user.getUserId().toString());
                    userLoginData.setName(user.getName());
                    userLoginData.setEmail(user.getEmail());
                    response.setData(userLoginData);
                    response.setOk(true);

                } else {
                    response.setError(true);
                    response.setMessage("⚠️ Wrong Email or password");
                }
            } else {
                response.setError(true);
                response.setMessage("⚠️ Unregistered email");
            }           
        }
        return response;
    }

    public List<User> get() {
        return userRepository.findAll();
    }

    public String setJwt(UserLoginDTO userLoginDTO) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        String token;
        LocalDateTime dateTime = LocalDateTime.now().plusMinutes(100);
        Date expireTime = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());

        token = JWT.create()
            .withClaim("userId", userLoginDTO.getUserId())
            .withClaim("email", userLoginDTO.getEmail())
            .withClaim("name", userLoginDTO.getName())
            .withExpiresAt(expireTime)
            .sign(algorithm);
        
        return token;
    }

    public UserLoginDTO decodedJwt(String cookie) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        JWTVerifier verifier = JWT.require(algorithm)
                                       .build(); // reusable verifier
        DecodedJWT decodedJWT = verifier.verify(cookie);

        String userId = decodedJWT.getClaim("userId").asString();
        String email = decodedJWT.getClaim("email").asString();
        String name = decodedJWT.getClaim("name").asString();
        UserLoginDTO userLoginData = new UserLoginDTO();
        userLoginData.setUserId(userId);
        userLoginData.setName(email);
        userLoginData.setEmail(name);
        return userLoginData;
    }

    public UserDTO getUserById (Integer userId) {
        User user = userRepository.getUserByUserId(userId);
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }
}