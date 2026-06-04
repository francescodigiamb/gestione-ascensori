package com.francesco.gestione_ascensori.controller;

import com.francesco.gestione_ascensori.model.RuoloUtente;
import com.francesco.gestione_ascensori.model.Utente;
import com.francesco.gestione_ascensori.repository.UtenteRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UtenteRepository utenteRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminController(UtenteRepository utenteRepository, PasswordEncoder passwordEncoder) {
        this.utenteRepository = utenteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String admin(Model model) {
        model.addAttribute("utenti", utenteRepository.findAll());
        return "admin/utenti";
    }

    // Reset password di un utente (solo admin)
    @PostMapping("/utenti/{id}/password")
    public String resetPassword(@PathVariable Long id,
            @RequestParam("nuovaPassword") String nuovaPassword,
            RedirectAttributes ra) {
        Utente utente = utenteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato: " + id));
        utente.setPassword(passwordEncoder.encode(nuovaPassword));
        utenteRepository.save(utente);
        ra.addFlashAttribute("msg", "Password aggiornata per " + utente.getNomeCompleto());
        return "redirect:/admin";
    }

    // Modifica nome completo
    @PostMapping("/utenti/{id}/nome")
    public String aggiornaNome(@PathVariable Long id,
            @RequestParam("nomeCompleto") String nomeCompleto,
            RedirectAttributes ra) {
        Utente utente = utenteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato: " + id));
        utente.setNomeCompleto(nomeCompleto);
        utenteRepository.save(utente);
        ra.addFlashAttribute("msg", "Nome aggiornato.");
        return "redirect:/admin";
    }
}
