package com.francesco.gestione_ascensori.controller;

import com.francesco.gestione_ascensori.model.Impianto;
import com.francesco.gestione_ascensori.model.Intervento;
import com.francesco.gestione_ascensori.model.Luogo;
import com.francesco.gestione_ascensori.model.StatoImpianto;
import com.francesco.gestione_ascensori.model.StatoIntervento;
import com.francesco.gestione_ascensori.repository.ImpiantoRepository;
import com.francesco.gestione_ascensori.repository.InterventoRepository;
import com.francesco.gestione_ascensori.repository.LuogoRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class ImpiantoController {

    private final ImpiantoRepository impiantoRepository;
    private final InterventoRepository interventoRepository;
    private final LuogoRepository luogoRepository;

    public ImpiantoController(ImpiantoRepository impiantoRepository,
            InterventoRepository interventoRepository, LuogoRepository luogoRepository) {
        this.impiantoRepository = impiantoRepository;
        this.interventoRepository = interventoRepository;
        this.luogoRepository = luogoRepository;
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

    // ✅ FORM NUOVO IMPIANTO
    @GetMapping("/luoghi/{luogoId}/impianti/nuovo")
    public String mostraFormNuovoImpianto(@PathVariable Long luogoId, Model model) {

        // Recupero il luogo per associare l'impianto
        Luogo luogo = luogoRepository.findById(luogoId)
                .orElseThrow(() -> new IllegalArgumentException("Luogo non trovato: " + luogoId));

        // Impianto vuoto solo per eventuali binding futuri
        Impianto impianto = new Impianto();
        impianto.setLuogo(luogo);

        model.addAttribute("luogo", luogo);
        model.addAttribute("impianto", impianto);
        model.addAttribute("statiImpianto", StatoImpianto.values());

        // per gestire il titolo del form
        model.addAttribute("mode", "create");
        model.addAttribute("formAction", "/luoghi/" + luogoId + "/impianti/nuovo");

        model.addAttribute("pageTitle", "Nuovo impianto - " + luogo.getNome());

        return "impianto-form";
    }

    // ✅ SALVA NUOVO IMPIANTO
    @PostMapping("/luoghi/{luogoId}/impianti/nuovo")
    public String salvaNuovoImpianto(@PathVariable Long luogoId,
            @RequestParam("nome") String nome,
            @RequestParam("indirizzo") String indirizzo,
            @RequestParam("stato") String stato,
            @RequestParam(value = "note", required = false) String note) {

        Luogo luogo = luogoRepository.findById(luogoId)
                .orElseThrow(() -> new IllegalArgumentException("Luogo non trovato: " + luogoId));

        Impianto impianto = new Impianto();
        impianto.setNome(nome);
        impianto.setIndirizzo(indirizzo);
        impianto.setLuogo(luogo);
        impianto.setStato(StatoImpianto.valueOf(stato));
        impianto.setNote(note);

        impiantoRepository.save(impianto);

        // Torniamo alla lista impianti di quel luogo
        return "redirect:/luoghi/" + luogoId;
    }

    // ✅ FORM MODIFICA IMPIANTO
    @GetMapping("/impianti/{id}/modifica")
    public String mostraFormModificaImpianto(@PathVariable Long id, Model model) {

        Impianto impianto = impiantoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Impianto non trovato: " + id));

        Luogo luogo = impianto.getLuogo();

        model.addAttribute("luogo", luogo);
        model.addAttribute("impianto", impianto);
        model.addAttribute("statiImpianto", StatoImpianto.values());

        model.addAttribute("mode", "edit");
        model.addAttribute("formAction", "/impianti/" + id + "/modifica");
        model.addAttribute("pageTitle", "Modifica impianto - " + impianto.getNome());

        return "impianto-form";
    }

    // ✅ SALVA MODIFICA IMPIANTO
    @PostMapping("/impianti/{id}/modifica")
    public String salvaModificaImpianto(@PathVariable Long id,
            @RequestParam("nome") String nome,
            @RequestParam("indirizzo") String indirizzo,
            @RequestParam("stato") String stato,
            @RequestParam(value = "note", required = false) String note) {

        Impianto impianto = impiantoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Impianto non trovato: " + id));

        impianto.setNome(nome);
        impianto.setIndirizzo(indirizzo);
        impianto.setStato(StatoImpianto.valueOf(stato));
        impianto.setNote(note);

        impiantoRepository.save(impianto);

        Long luogoId = impianto.getLuogo().getId();
        return "redirect:/luoghi/" + luogoId;
    }

}
