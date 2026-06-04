package com.francesco.gestione_ascensori.controller;

import com.francesco.gestione_ascensori.model.*;
import com.francesco.gestione_ascensori.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {

    private final LuogoRepository luogoRepository;
    private final ImpiantoRepository impiantoRepository;
    private final InterventoRepository interventoRepository;
    private final UtenteRepository utenteRepository;
    private final GiornataLavorativaRepository giornataRepository;

    public HomeController(LuogoRepository luogoRepository,
                          ImpiantoRepository impiantoRepository,
                          InterventoRepository interventoRepository,
                          UtenteRepository utenteRepository,
                          GiornataLavorativaRepository giornataRepository) {
        this.luogoRepository = luogoRepository;
        this.impiantoRepository = impiantoRepository;
        this.interventoRepository = interventoRepository;
        this.utenteRepository = utenteRepository;
        this.giornataRepository = giornataRepository;
    }

    @GetMapping("/")
    public String home(Model model, Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        model.addAttribute("luoghi", luogoRepository.findAll());
        model.addAttribute("isHomePage", true);

        if (isAdmin) {
            model.addAttribute("impiantiAttivi",    impiantoRepository.count());
            model.addAttribute("interventiDaFare",  interventoRepository.countByStato(StatoIntervento.DA_FARE));
            model.addAttribute("interventiInCorso", interventoRepository.countByStato(StatoIntervento.IN_CORSO));
            model.addAttribute("inScadenza",
                    interventoRepository.findInScadenzaEntro(StatoIntervento.DA_FARE, LocalDate.now().plusDays(7)));
            model.addAttribute("oggi", LocalDate.now());
        } else {
            Utente utente = utenteRepository.findByUsername(auth.getName()).orElseThrow();
            List<Intervento> mieiInterventi = interventoRepository.findByAssegnatario(utente);
            Optional<GiornataLavorativa> giornataOggi =
                    giornataRepository.findByUtenteAndData(utente, LocalDate.now());

            model.addAttribute("mieiInterventi", mieiInterventi);
            model.addAttribute("giornataOggi", giornataOggi.orElse(null));
            model.addAttribute("oggi", LocalDate.now());
        }

        return "index";
    }
}
