package com.holamundo.HOLASPRING6CV3.config;

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
            .csrf().disable()  // Deshabilita CSRF (Opcional, pero revisa si lo necesitas)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/registro", "/login", "/testConnection", "/css/**", "/js/**").permitAll() // Permitir acceso a login y registro
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/user/**").hasRole("USER")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login") // Página de login personalizada
                .loginProcessingUrl("/procesar_login") // URL donde se envían los datos del formulario
                .defaultSuccessUrl("/home", true) // Redirigir tras un login exitoso
                .failureUrl("/login?error=true") // Redirigir en caso de error
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
