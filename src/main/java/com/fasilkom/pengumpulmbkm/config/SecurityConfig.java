package com.fasilkom.pengumpulmbkm.config;


import com.fasilkom.skripsi.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        prePostEnabled = true)
public class SecurityConfig{

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

//    @Bean
//    AuthenticationManager authenticationManager(AuthenticationManagerBuilder builder) throws Exception {
//        return builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder()).and().build();
//    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/v3/api-docs/**","/swagger-ui.html", "/swagger-ui/**");
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf()
                .disable()
                .exceptionHandling()
                .authenticationEntryPoint(unauthorizedHandler)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .requestMatchers("/swagger-ui/**")
                .permitAll()
                .requestMatchers("/api/auth/signin")
                .permitAll()
                .anyRequest()
                .authenticated();
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
//    @Bean
//    protected void configure(HttpSecurity http) throws Exception {
//        http
//                .cors().disable()
//                .csrf().disable()
//                .exceptionHandling()
//                .authenticationEntryPoint(unauthorizedHandler)
//                .and()
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .authorizeRequests()
//                .antMatchers("/api/auth/signin")
//                .permitAll()
//                .antMatchers("/api/auth/signup")
//                .permitAll()
//                .antMatchers("/**")
//                .permitAll()
//                .antMatchers("/api/auth/user/**")
//                .hasAuthority(ERole.USER.name())
//                .antMatchers("/api/test/**")
//                .permitAll()
//                .antMatchers("/tiket/**")
//                .hasAuthority(ERole.USER.name())
//                .antMatchers("http://localhost:8080/ws")
//                .permitAll()
//                .anyRequest()
//                .authenticated();
//
//
//        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
//    }

}
