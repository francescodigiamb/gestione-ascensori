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

        // Tutti gli interventi dell'impianto
        List<Intervento> interventi = interventoRepository.findByImpianto(impianto);

        // Calcolo totale dei costi COMPLETATI (senza lambda/stream)
        BigDecimal totaleCosti = BigDecimal.ZERO;
        for (Intervento i : interventi) {
            if (i.getStato() == StatoIntervento.COMPLETATO && i.getCosto() != null) {
                totaleCosti = totaleCosti.add(i.getCosto());
            }
        }

        model.addAttribute("impianto", impianto);
        model.addAttribute("interventi", interventi);
        model.addAttribute("totaleCosti", totaleCosti);
        model.addAttribute("pageTitle", impianto.getNome());

        return "impianto-dettaglio";
    }
}
