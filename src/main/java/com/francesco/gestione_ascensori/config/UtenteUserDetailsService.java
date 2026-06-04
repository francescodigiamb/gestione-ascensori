package com.francesco.gestione_ascensori.config;

import com.francesco.gestione_ascensori.model.Utente;
import com.francesco.gestione_ascensori.repository.UtenteRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UtenteUserDetailsService implements UserDetailsService {

    private final UtenteRepository utenteRepository;

    public UtenteUserDetailsService(UtenteRepository utenteRepository) {
        this.utenteRepository = utenteRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Utente utente = utenteRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato: " + username));

        return User.builder()
                .username(utente.getUsername())
                .password(utente.getPassword())
                .roles(utente.getRuolo().name())
                .build();
    }
}
