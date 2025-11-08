package com.garritas.sgv.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.garritas.sgv.repository.UsuarioRepository;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final UsuarioRepository usuarioRepository;

    public WebSecurityConfig(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
        System.out.println("Buscando usuario: " + username);
        return usuarioRepository.findByCodigo(username)
            .map(user -> User.withUsername(user.getCodigo())
                    .password(user.getContrasena())
                    .roles(user.getIdCargo().getNombre())
                    .build())
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado."));
    };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                    // Rutas públicas (Accesibles por todos los usuarios)
                    .requestMatchers("/", "/inicio", "/login", "/registrar", "/restablecer", "error", "contacto", "servicios-info", "nosotros").permitAll()
                    .requestMatchers("/usuarios/**").authenticated()
                    .requestMatchers("/admin/**").hasRole("ADMIN")  // Solo ADMIN puede acceder
                    .requestMatchers("/veterinario/**").hasRole("VETERINARIO")  // Solo VETERINARIO puede acceder
                    .requestMatchers("/recepcionista/**").hasRole("RECEPCIONISTA")  // Solo RECEPCIONISTA puede acceder
                    .requestMatchers("/cliente/**").hasRole("CLIENTE")  // Solo CLIENTE puede acceder
                    // Permitir acceso a archivos estáticos (CSS, JS, Imagenes)
                    .requestMatchers("/static/**", "/CSS/**", "/Imagenes/**", "/JS/**").permitAll()
                    .anyRequest().authenticated()  // El resto de las rutas requieren autenticación
            )
            .formLogin(formLogin -> formLogin
                .loginPage("/login")
                .defaultSuccessUrl("/menu", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            )
            .exceptionHandling(exceptionHandling -> exceptionHandling
            .accessDeniedPage("/acceso-denegado"));
        return http.build();
    }
}
