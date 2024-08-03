package com.apboutos.spendy.spendyapi.configuration;

import com.apboutos.spendy.spendyapi.security.JWTAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final AuthenticationProvider authenticationProvider;

    private final JWTAuthenticationFilter jwtAuthenticationFilter;


    private static final String ENTRIES = "/api/v1/entries";
    private static final String CATEGORIES = "/api/v1/categories";
    private static final String USERS = "/api/v1/users/**";
    private static final String ACTUATOR = "/actuator/**";


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.cors(Customizer.withDefaults());

        http.authorizeHttpRequests(request -> {
            request.requestMatchers(ACTUATOR).permitAll();
            request.requestMatchers(HttpMethod.POST, USERS).permitAll();
            request.requestMatchers(HttpMethod.PATCH, USERS).permitAll();
            request.requestMatchers(HttpMethod.GET, CATEGORIES).hasAuthority("USER");
            request.requestMatchers(HttpMethod.POST, CATEGORIES).hasAuthority("USER");
            request.requestMatchers(HttpMethod.PUT, CATEGORIES).hasAuthority("USER");
            request.requestMatchers(HttpMethod.GET, ENTRIES).hasAuthority("USER");
            request.requestMatchers(HttpMethod.POST, ENTRIES).hasAuthority("USER");
            request.requestMatchers(HttpMethod.PUT, ENTRIES).hasAuthority("USER");
            request.requestMatchers(HttpMethod.DELETE, ENTRIES).hasAuthority("USER");
            request.anyRequest().authenticated();
        });
        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("https://spendy-website-0daf18acdc8b.herokuapp.com"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
