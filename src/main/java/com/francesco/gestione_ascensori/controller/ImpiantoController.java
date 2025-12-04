package com.francesco.gestione_ascensori.controller;

import com.francesco.gestione_ascensori.model.Impianto;
import com.francesco.gestione_ascensori.model.Intervento;
import com.francesco.gestione_ascensori.model.StatoIntervento;
import com.francesco.gestione_ascensori.repository.ImpiantoRepository;
import com.francesco.gestione_ascensori.repository.InterventoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class ImpiantoController {

    private final ImpiantoRepository impiantoRepository;
    private final InterventoRepository interventoRepository;

    public ImpiantoController(ImpiantoRepository impiantoRepository,
            InterventoRepository interventoRepository) {
        this.impiantoRepository = impiantoRepository;
        this.interventoRepository = interventoRepository;
    }

    @GetMapping("/impianti/{id}")
    public String dettaglioImpianto(@PathVariable Long id, Model model) {

        // Recuperiamo l’impianto o errore
        Impianto impianto = impiantoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Impianto non trovato: " + id));

        // Interventi associati
        List<Intervento> interventi = interventoRepository.findByImpianto(impianto);

        // Calcolo totale dei costi completati
        BigDecimal totaleCosti = interventi.stream()
                .filter(i -> i.getStato() == StatoIntervento.COMPLETATO && i.getCosto() != null)
                .map(Intervento::getCosto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("impianto", impianto);
        model.addAttribute("interventi", interventi);
        model.addAttribute("totaleCosti", totaleCosti);
        model.addAttribute("pageTitle", impianto.getNome());

        return "impianto-dettaglio";
    }
}
