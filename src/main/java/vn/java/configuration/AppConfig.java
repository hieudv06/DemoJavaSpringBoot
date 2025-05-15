package vn.java.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import vn.java.service.UserService;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@Profile("!prod")
@RequiredArgsConstructor
public class AppConfig {

    private final UserService userService;
    private final PreFilter preFilter;

    private String[] WHITE_LIST = {"/auth/**"};


    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer(){
            @Override
            public void addCorsMappings(CorsRegistry registry){
                registry.addMapping("**")
                        .allowedOrigins("http://localhost:8080")
                        .allowedMethods("GET","POST","PUT","DELETE")
                        .allowCredentials(false)
                        .maxAge(3600);
            }
        };
    }

    @Bean
    public PasswordEncoder getPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain configure (HttpSecurity http) throws Exception{
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequests ->authorizeRequests
                        .requestMatchers(WHITE_LIST).permitAll().anyRequest().authenticated())
                .sessionManagement(manager -> manager.sessionCreationPolicy(STATELESS))
                        .authenticationProvider(provider()).addFilterBefore(preFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationProvider provider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService.userDetailService());
        provider.setPasswordEncoder(getPasswordEncoder());

        return provider;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return webSecurity ->
                webSecurity.ignoring()
                        .requestMatchers("/actuator/**", "/v3/**"
                                , "/webjars/**", "/swagger-ui*/*swagger-initializer.js",
                                "/swagger-ui*/**");
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return  config.getAuthenticationManager();
    }

}
