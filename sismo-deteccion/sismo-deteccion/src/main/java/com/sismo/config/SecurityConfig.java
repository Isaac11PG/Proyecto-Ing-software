package com.sismo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()  // Deshabilita CSRF (Opcional, pero asegúrate de que es lo que necesitas)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/registro", "/login", "/testConnection", "/css/**", "/js/**", "/api/registro").permitAll() // Permitir acceso público
                .requestMatchers("/api/admin/**").hasRole("ADMIN") // Acceso solo para ADMIN
                .requestMatchers("/api/user/**").hasRole("USER") // Acceso solo para USER
                .anyRequest().authenticated() // Todas las demás rutas requieren autenticación
            )
            .formLogin(form -> form
                .loginPage("/login") // Página de login personalizada
                .loginProcessingUrl("/procesar_login") // URL donde se envían los datos del formulario de login
                .defaultSuccessUrl("/home", true) // Redirigir tras un login exitoso
                .failureUrl("/login?error=true") // Redirigir en caso de error en el login
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout") // URL para cerrar sesión
                .logoutSuccessUrl("/login?logout=true") // Redirigir tras cerrar sesión
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Usar BCrypt para encriptar contraseñas
    }
}