package com.francesco.gestione_ascensori.controller;

import com.francesco.gestione_ascensori.model.GiornataLavorativa;
import com.francesco.gestione_ascensori.model.Utente;
import com.francesco.gestione_ascensori.repository.GiornataLavorativaRepository;
import com.francesco.gestione_ascensori.repository.UtenteRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Controller
@RequestMapping("/giornata")
public class GiornataController {

    private final GiornataLavorativaRepository giornataRepository;
    private final UtenteRepository utenteRepository;

    public GiornataController(GiornataLavorativaRepository giornataRepository,
                               UtenteRepository utenteRepository) {
        this.giornataRepository = giornataRepository;
        this.utenteRepository = utenteRepository;
    }

    // Operatore preme "Inizia giornata" — salva oraInizio = adesso
    @PostMapping("/inizia")
    public String inizia(Authentication auth) {
        Utente utente = utenteRepository.findByUsername(auth.getName()).orElseThrow();
        LocalDate oggi = LocalDate.now();

        Optional<GiornataLavorativa> esistente = giornataRepository.findByUtenteAndData(utente, oggi);
        if (esistente.isEmpty()) {
            GiornataLavorativa g = new GiornataLavorativa(utente, oggi, LocalTime.now());
            giornataRepository.save(g);
        }
        return "redirect:/";
    }

    // Operatore preme "Termina giornata" — salva oraFine = adesso e blocca
    @PostMapping("/termina")
    public String termina(Authentication auth) {
        Utente utente = utenteRepository.findByUsername(auth.getName()).orElseThrow();
        LocalDate oggi = LocalDate.now();

        giornataRepository.findByUtenteAndData(utente, oggi).ifPresent(g -> {
            if (!g.isBloccata()) {
                g.setOraFine(LocalTime.now());
                g.setBloccata(true);
                giornataRepository.save(g);
            }
        });
        return "redirect:/";
    }
}
