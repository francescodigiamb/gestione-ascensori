package com.francesco.gestione_ascensori.controller;

import com.francesco.gestione_ascensori.model.StatoImpianto;
import com.francesco.gestione_ascensori.model.StatoIntervento;
import com.francesco.gestione_ascensori.model.Intervento;
import com.francesco.gestione_ascensori.repository.ImpiantoRepository;
import com.francesco.gestione_ascensori.repository.InterventoRepository;
import com.francesco.gestione_ascensori.repository.LuogoRepository;
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

    public HomeController(LuogoRepository luogoRepository,
                          ImpiantoRepository impiantoRepository,
                          InterventoRepository interventoRepository) {
        this.luogoRepository = luogoRepository;
        this.impiantoRepository = impiantoRepository;
        this.interventoRepository = interventoRepository;
    }

    @GetMapping("/")
    public String home(Model model) {

        long impiantiAttivi   = impiantoRepository.countByStato(StatoImpianto.ATTIVO);
        long interventiDaFare = interventoRepository.countByStato(StatoIntervento.DA_FARE);
        long interventiInCorso = interventoRepository.countByStato(StatoIntervento.IN_CORSO);

        List<Intervento> inScadenza = interventoRepository
                .findInScadenzaEntro(StatoIntervento.DA_FARE, LocalDate.now().plusDays(7));

        model.addAttribute("impiantiAttivi",    impiantiAttivi);
        model.addAttribute("interventiDaFare",  interventiDaFare);
        model.addAttribute("interventiInCorso", interventiInCorso);
        model.addAttribute("inScadenza",        inScadenza);
        model.addAttribute("oggi",              LocalDate.now());
        model.addAttribute("luoghi",            luogoRepository.findAll());
        model.addAttribute("isHomePage",        true);

        return "index";
    }
}
