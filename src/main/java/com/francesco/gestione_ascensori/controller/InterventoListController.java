package com.francesco.gestione_ascensori.controller;

import com.francesco.gestione_ascensori.model.Intervento;
import com.francesco.gestione_ascensori.model.StatoIntervento;
import com.francesco.gestione_ascensori.repository.InterventoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class InterventoListController {

    private final InterventoRepository interventoRepository;

    public InterventoListController(InterventoRepository interventoRepository) {
        this.interventoRepository = interventoRepository;
    }

    @GetMapping("/interventi")
    public String lista(@RequestParam(value = "stato", required = false) String statoStr, Model model) {

        List<Intervento> interventi;
        StatoIntervento statoFiltro = null;

        if (statoStr != null && !statoStr.isBlank()) {
            try {
                statoFiltro = StatoIntervento.valueOf(statoStr);
                interventi = interventoRepository.findByStato(statoFiltro);
            } catch (IllegalArgumentException e) {
                interventi = interventoRepository.findAll();
            }
        } else {
            interventi = interventoRepository.findAll();
        }

        model.addAttribute("interventi", interventi);
        model.addAttribute("statoFiltro", statoFiltro);
        model.addAttribute("statiIntervento", StatoIntervento.values());
        return "interventi-lista";
    }
}
