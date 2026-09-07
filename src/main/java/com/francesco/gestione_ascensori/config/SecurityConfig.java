package com.francesco.gestione_ascensori.config;

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
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/img/**").permitAll()
                // Sezione admin
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // Creazione e modifica luoghi/impianti — solo admin
                .requestMatchers("/luoghi/nuovo",
                                 "/luoghi/*/impianti/nuovo",
                                 "/impianti/*/modifica",
                                 "/impianti/*/elimina").hasRole("ADMIN")
                // Download dei rapportini in Word — solo admin
                .requestMatchers("/interventi/*/rapportino.docx").hasRole("ADMIN")
                .anyRequest().authenticated())
            // Pagina non consentita per il ruolo: si torna alla dashboard,
            // niente pagina di errore
            .exceptionHandling(ex -> ex.accessDeniedHandler((request, response, negato) ->
                response.sendRedirect(request.getContextPath() + "/")))
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll())
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
