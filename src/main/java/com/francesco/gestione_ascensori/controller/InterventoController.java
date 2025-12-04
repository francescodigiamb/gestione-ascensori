package com.francesco.gestione_ascensori.controller;

import com.francesco.gestione_ascensori.model.Impianto;
import com.francesco.gestione_ascensori.model.Intervento;
import com.francesco.gestione_ascensori.model.StatoIntervento;
import com.francesco.gestione_ascensori.model.TipoIntervento;
import com.francesco.gestione_ascensori.repository.ImpiantoRepository;
import com.francesco.gestione_ascensori.repository.InterventoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
public class InterventoController {

    private final ImpiantoRepository impiantoRepository;
    private final InterventoRepository interventoRepository;

    public InterventoController(ImpiantoRepository impiantoRepository,
            InterventoRepository interventoRepository) {
        this.impiantoRepository = impiantoRepository;
        this.interventoRepository = interventoRepository;
    }

    // ✅ FORM NUOVO INTERVENTO
    @GetMapping("/impianti/{impiantoId}/interventi/nuovo")
    public String mostraFormNuovoIntervento(@PathVariable Long impiantoId, Model model) {

        Impianto impianto = impiantoRepository.findById(impiantoId)
                .orElseThrow(() -> new IllegalArgumentException("Impianto non trovato: " + impiantoId));

        model.addAttribute("impianto", impianto);
        model.addAttribute("tipiIntervento", TipoIntervento.values());
        model.addAttribute("statiIntervento", StatoIntervento.values());

        // modalità create (nuovo)
        model.addAttribute("mode", "create");
        // URL a cui inviare il form
        model.addAttribute("formAction", "/impianti/" + impiantoId + "/interventi/nuovo");

        return "intervento-form";
    }

    // ✅ SALVA NUOVO INTERVENTO (POST)
    @PostMapping("/impianti/{impiantoId}/interventi/nuovo")
    public String salvaNuovoIntervento(@PathVariable Long impiantoId,
            @RequestParam("tipo") String tipo,
            @RequestParam("stato") String stato,
            @RequestParam("descrizione") String descrizione,
            @RequestParam(value = "costo", required = false) BigDecimal costo) {

        Impianto impianto = impiantoRepository.findById(impiantoId)
                .orElseThrow(() -> new IllegalArgumentException("Impianto non trovato: " + impiantoId));

        Intervento intervento = new Intervento();
        intervento.setImpianto(impianto);
        intervento.setTipo(TipoIntervento.valueOf(tipo));
        intervento.setStato(StatoIntervento.valueOf(stato));
        intervento.setDescrizione(descrizione);
        intervento.setCosto(costo); // può essere null

        interventoRepository.save(intervento);

        // Torniamo alla pagina dettaglio impianto
        return "redirect:/impianti/" + impiantoId;
    }

    // ✅ FORM MODIFICA INTERVENTO
    @GetMapping("/interventi/{id}/modifica")
    public String mostraFormModificaIntervento(@PathVariable Long id, Model model) {

        Intervento intervento = interventoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Intervento non trovato: " + id));

        Impianto impianto = intervento.getImpianto();

        model.addAttribute("impianto", impianto);
        model.addAttribute("intervento", intervento);
        model.addAttribute("tipiIntervento", TipoIntervento.values());
        model.addAttribute("statiIntervento", StatoIntervento.values());

        // modalità edit (modifica)
        model.addAttribute("mode", "edit");
        model.addAttribute("formAction", "/interventi/" + id + "/modifica");

        return "intervento-form";
    }

    // ✅ SALVA MODIFICHE INTERVENTO
    @PostMapping("/interventi/{id}/modifica")
    public String salvaModificaIntervento(@PathVariable Long id,
            @RequestParam("tipo") String tipo,
            @RequestParam("stato") String stato,
            @RequestParam("descrizione") String descrizione,
            @RequestParam(value = "costo", required = false) BigDecimal costo) {

        Intervento intervento = interventoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Intervento non trovato: " + id));

        intervento.setTipo(TipoIntervento.valueOf(tipo));
        intervento.setStato(StatoIntervento.valueOf(stato));
        intervento.setDescrizione(descrizione);
        intervento.setCosto(costo);

        interventoRepository.save(intervento);

        Long impiantoId = intervento.getImpianto().getId();
        return "redirect:/impianti/" + impiantoId;
    }
}
