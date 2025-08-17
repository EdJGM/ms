package com.auction.auction.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    private final AuthTokenFilter authTokenFilter;
    public WebSecurityConfig(AuthTokenFilter authTokenFilter) {
        this.authTokenFilter = authTokenFilter;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.disable())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/auctions").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                // Productos - Solo MODERADOR y ADMINISTRADOR pueden crear/modificar
                .requestMatchers(HttpMethod.POST, "/productos").hasAnyRole("MODERADOR", "ADMINISTRADOR")
                .requestMatchers(HttpMethod.PUT, "/productos/**").hasAnyRole("MODERADOR", "ADMINISTRADOR")
                .requestMatchers(HttpMethod.DELETE, "/productos/**").hasAnyRole("MODERADOR", "ADMINISTRADOR")
                .requestMatchers(HttpMethod.GET, "/productos/**").permitAll()
                // Subastas - Solo MODERADOR y ADMINISTRADOR pueden crear/modificar
                .requestMatchers(HttpMethod.POST, "/subastas").hasAnyRole("MODERADOR", "ADMINISTRADOR")
                .requestMatchers(HttpMethod.PUT, "/subastas/**").hasAnyRole("MODERADOR", "ADMINISTRADOR")
                .requestMatchers(HttpMethod.DELETE, "/subastas/**").hasAnyRole("MODERADOR", "ADMINISTRADOR")
                .requestMatchers(HttpMethod.GET, "/subastas/**").permitAll()
                // Cualquier otra petición requiere autenticación
                .anyRequest().authenticated()
            );
        http.addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
