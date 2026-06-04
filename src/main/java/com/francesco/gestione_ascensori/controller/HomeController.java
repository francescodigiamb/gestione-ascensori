package com.francesco.gestione_ascensori.controller;

import com.francesco.gestione_ascensori.model.Intervento;
import com.francesco.gestione_ascensori.model.StatoImpianto;
import com.francesco.gestione_ascensori.model.StatoIntervento;
import com.francesco.gestione_ascensori.model.Utente;
import com.francesco.gestione_ascensori.repository.ImpiantoRepository;
import com.francesco.gestione_ascensori.repository.InterventoRepository;
import com.francesco.gestione_ascensori.repository.LuogoRepository;
import com.francesco.gestione_ascensori.repository.UtenteRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
public class HomeController {

    private final LuogoRepository luogoRepository;
    private final ImpiantoRepository impiantoRepository;
    private final InterventoRepository interventoRepository;
    private final UtenteRepository utenteRepository;

    public HomeController(LuogoRepository luogoRepository,
                          ImpiantoRepository impiantoRepository,
                          InterventoRepository interventoRepository,
                          UtenteRepository utenteRepository) {
        this.luogoRepository = luogoRepository;
        this.impiantoRepository = impiantoRepository;
        this.interventoRepository = interventoRepository;
        this.utenteRepository = utenteRepository;
    }

    @GetMapping("/")
    public String home(Model model, Authentication auth) {

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        model.addAttribute("luoghi", luogoRepository.findAll());
        model.addAttribute("isHomePage", true);

        if (isAdmin) {
            // Dashboard admin
            model.addAttribute("impiantiAttivi",    impiantoRepository.count());
            model.addAttribute("interventiDaFare",  interventoRepository.countByStato(StatoIntervento.DA_FARE));
            model.addAttribute("interventiInCorso", interventoRepository.countByStato(StatoIntervento.IN_CORSO));
            model.addAttribute("inScadenza",
                    interventoRepository.findInScadenzaEntro(StatoIntervento.DA_FARE, LocalDate.now().plusDays(7)));
            model.addAttribute("oggi", LocalDate.now());
        } else {
            // Home operatore: mostra i suoi interventi assegnati
            Utente utente = utenteRepository.findByUsername(auth.getName()).orElseThrow();
            List<Intervento> mieiInterventi = interventoRepository.findByAssegnatario(utente);
            model.addAttribute("mieiInterventi", mieiInterventi);
            model.addAttribute("oggi", LocalDate.now());
        }

        return "index";
    }
}
