package com.peiyingr.yummyfit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private static final String[] WHITE_LIST = {
        "/**",
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        // 跨域請求設定: 先用預設
        // .cors(Customizer.withDefaults())
        // stateless API使用token驗證時, 不需要csrf保護
        .csrf(csrf -> csrf.disable())
        // 驗證API的規則, 依序處理
        .authorizeHttpRequests(authorize -> authorize
                     // (1). 符合特定URL規則, 直接通過
                     .requestMatchers(WHITE_LIST).permitAll()
                     // (2). 任何請求皆須經過驗證
                     .anyRequest().authenticated());
         // 登入後, 轉導的頁面
        //  .formLogin(form -> form -> form.loginProcessingUrl("/"))
         // 登出所使用的url
        //  .logout(logout -> logout.logoutUrl("/logout"));

        return http.build();
    }

}