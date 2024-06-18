package com.example.ergasiapssd.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth
                            .requestMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**", "/vendor/**", "/fonts/**").permitAll()
                            .requestMatchers("/index", "/", "/error/**").permitAll()
                            .requestMatchers("/users").hasAuthority("ROLE_ADMIN")
                            .requestMatchers("/users/studentUsernames").hasAnyAuthority("ROLE_ADMIN", "ROLE_TEACHER")
                            .requestMatchers("/users/logout").hasAnyAuthority("ROLE_ADMIN", "ROLE_STUDENT", "ROLE_TEACHER")
                            .requestMatchers("/quizzes/create", "/quizzes/myQuizzes").hasAuthority("ROLE_TEACHER")
                            .requestMatchers("/quizzes/solve/*").hasAuthority("ROLE_STUDENT")
                            .requestMatchers("/users/**").hasAuthority("ROLE_ANONYMOUS") // restrict access to login/register page for loggedIn users
                            .anyRequest().authenticated();
                })
                .sessionManagement(ses -> ses.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .formLogin(login -> {
                    login.loginPage("/users/loginToAccess");
                    login.failureUrl("/index");
                    login.failureForwardUrl("/index");
                })
                .logout(logout -> {
                    logout.logoutSuccessUrl("/index")
                            .addLogoutHandler(logoutHandler)
                            .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext());
                });

        return httpSecurity.build();
    }
}
