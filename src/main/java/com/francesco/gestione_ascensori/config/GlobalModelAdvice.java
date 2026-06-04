package com.francesco.gestione_ascensori.config;

import com.francesco.gestione_ascensori.model.StatoIntervento;
import com.francesco.gestione_ascensori.model.Utente;
import com.francesco.gestione_ascensori.repository.InterventoRepository;
import com.francesco.gestione_ascensori.repository.UtenteRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDate;

@ControllerAdvice
public class GlobalModelAdvice {

    private final InterventoRepository interventoRepository;
    private final UtenteRepository utenteRepository;

    public GlobalModelAdvice(InterventoRepository interventoRepository,
                             UtenteRepository utenteRepository) {
        this.interventoRepository = interventoRepository;
        this.utenteRepository = utenteRepository;
    }

    @ModelAttribute("navbarAlertCount")
    public long navbarAlertCount() {
        return interventoRepository.countInScadenzaEntro(
                StatoIntervento.DA_FARE, LocalDate.now().plusDays(7));
    }

    @ModelAttribute("utenteCorrente")
    public Utente utenteCorrente() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()
                || "anonymousUser".equals(auth.getPrincipal())) return null;
        return utenteRepository.findByUsername(auth.getName()).orElse(null);
    }

    @ModelAttribute("isAdmin")
    public boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
