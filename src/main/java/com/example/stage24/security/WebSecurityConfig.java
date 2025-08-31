package com.example.stage24.security;

import com.example.stage24.security.jwt.AuthEntryPointJwt;
import com.example.stage24.security.jwt.AuthTokenFilter;
import com.example.stage24.security.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;

@Configuration
//@EnableWebSecurity
@EnableMethodSecurity

public class WebSecurityConfig { // extends WebSecurityConfigurerAdapter {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }



    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/test/**").permitAll()
                        .requestMatchers("/api/agent/**").permitAll()
                        .requestMatchers("/api/article/search/by-website/**").permitAll()
                        .requestMatchers("/api/clients/**").permitAll()
                        .requestMatchers("/api/payments/**").permitAll()
                        .requestMatchers("/api/invoices/**").permitAll()
                        .requestMatchers("/api/about-us/**").permitAll()
                        .requestMatchers("/api/homepage1/**").permitAll()
                        .requestMatchers("/api/homepage2/**").permitAll()
                        .requestMatchers("/api/homepage3/**").permitAll()
                        .requestMatchers("/api/homepage/**").permitAll()
                        .requestMatchers("/api/profile/**").authenticated()
                        // Allow Stripe success and root pages without authentication
                        .requestMatchers("/", "/success").permitAll()
                        // Swagger/OpenAPI documentation endpoints
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        // Require auth for Stripe checkout creating session (uses JWT to identify user)
                        .requestMatchers("/product/v1/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/api/companies/**").permitAll()
                        .requestMatchers("/api/notifications/**").permitAll()
                        .requestMatchers("/api/documents/**").permitAll()
                        .requestMatchers("/api/email/**").permitAll()
                        .requestMatchers("/api/email-test/**").permitAll()
                        .requestMatchers("/api/article/details/**").permitAll()
                        .requestMatchers("/api/review/article/**").permitAll()
                        .requestMatchers("/api/article/by-website/**").permitAll()
                        .requestMatchers("/api/category/by-website/**").permitAll()
                        .requestMatchers("/api/files/room-plans/**").authenticated()
                        .requestMatchers("/api/files/**").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/websocket-example.html").permitAll()
                        .requestMatchers("/topic/**").permitAll()
                        .requestMatchers("/app/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
