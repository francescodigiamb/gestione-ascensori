package com.francesco.gestione_ascensori.controller;

import com.francesco.gestione_ascensori.model.Impianto;
import com.francesco.gestione_ascensori.model.StatoImpianto;
import com.francesco.gestione_ascensori.repository.ImpiantoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ImpiantoListController {

    private final ImpiantoRepository impiantoRepository;

    public ImpiantoListController(ImpiantoRepository impiantoRepository) {
        this.impiantoRepository = impiantoRepository;
    }

    @GetMapping("/impianti")
    public String lista(@RequestParam(value = "stato", required = false) String statoStr, Model model) {

        List<Impianto> impianti;
        StatoImpianto statoFiltro = null;

        if (statoStr != null && !statoStr.isBlank()) {
            try {
                statoFiltro = StatoImpianto.valueOf(statoStr);
                impianti = impiantoRepository.findByStato(statoFiltro);
            } catch (IllegalArgumentException e) {
                impianti = impiantoRepository.findAll();
            }
        } else {
            impianti = impiantoRepository.findAll();
        }

        model.addAttribute("impianti", impianti);
        model.addAttribute("statoFiltro", statoFiltro);
        model.addAttribute("statiImpianto", StatoImpianto.values());
        return "impianti-lista";
    }
}
