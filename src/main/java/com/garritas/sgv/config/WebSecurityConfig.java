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
        http.authorizeHttpRequests(authorizeRequests -> authorizeRequests
            // Rutas públicas (Accesibles por todos los usuarios)
            .requestMatchers("/", "/inicio", "/login", "/registrar", "/restablecer", "/error", "/contacto", "/servicios-info", "/nosotros").permitAll()
            // Rutas que solo requieren estar autenticado (sin importar el Rol)
            .requestMatchers("/menu", "usuarios/perfil").authenticated()
            // ==== Solo ADMIN ====
            .requestMatchers("/cargos/**", "/detalles/**", "/usuarios/**").hasRole("ADMIN")
            // ==== Rutas Compartidas por varios Roles ====
            .requestMatchers("/citas/**").hasAnyRole("ADMIN", "VETERINARIO", "RECEPCIONISTA", "CLIENTE")
            .requestMatchers("/clientes/**").hasAnyRole("ADMIN", "RECEPCIONISTA", "CLIENTE")
            .requestMatchers("/colas/**", "/inventarios/**").hasAnyRole("ADMIN", "VETERINARIO", "RECEPCIONISTA")
            .requestMatchers("/historiales/**").hasAnyRole("ADMIN", "VETERINARIO", "RECEPCIONISTA", "CLIENTE")
            .requestMatchers("/mascotas/**").hasAnyRole("ADMIN", "VETERINARIO", "RECEPCIONISTA", "CLIENTE")
            .requestMatchers("/servicios/**").hasAnyRole("ADMIN", "VETERINARIO", "CLIENTE")
            .requestMatchers("/veterinarios/**").hasAnyRole("ADMIN", "VETERINARIO")
             // Recursos estáticos
            .requestMatchers("/static/**", "/CSS/**", "/Imagenes/**", "/JS/**").permitAll()
            .anyRequest().authenticated()
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
