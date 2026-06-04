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
import java.time.LocalDate;
import java.time.LocalTime;

@Controller
public class InterventoController {

    private final ImpiantoRepository impiantoRepository;
    private final InterventoRepository interventoRepository;

    public InterventoController(ImpiantoRepository impiantoRepository,
            InterventoRepository interventoRepository) {
        this.impiantoRepository = impiantoRepository;
        this.interventoRepository = interventoRepository;
    }

    @GetMapping("/impianti/{impiantoId}/interventi/nuovo")
    public String mostraFormNuovo(@PathVariable Long impiantoId, Model model) {
        Impianto impianto = impiantoRepository.findById(impiantoId)
                .orElseThrow(() -> new IllegalArgumentException("Impianto non trovato: " + impiantoId));
        model.addAttribute("impianto", impianto);
        model.addAttribute("tipiIntervento", TipoIntervento.values());
        model.addAttribute("statiIntervento", StatoIntervento.values());
        model.addAttribute("mode", "create");
        model.addAttribute("formAction", "/impianti/" + impiantoId + "/interventi/nuovo");
        return "intervento-form";
    }

    @PostMapping("/impianti/{impiantoId}/interventi/nuovo")
    public String salvaNuovo(@PathVariable Long impiantoId,
            @RequestParam("tipo") String tipo,
            @RequestParam("stato") String stato,
            @RequestParam("descrizione") String descrizione,
            @RequestParam(value = "dataProgrammata", required = false) String dataProgrammataStr,
            @RequestParam(value = "dataEsecuzione",  required = false) String dataEsecuzioneStr,
            @RequestParam(value = "inizioIntervento", required = false) String inizioStr,
            @RequestParam(value = "fineIntervento",   required = false) String fineStr,
            @RequestParam(value = "noteTecnico", required = false) String noteTecnico,
            @RequestParam(value = "costo", required = false) BigDecimal costo) {

        Impianto impianto = impiantoRepository.findById(impiantoId)
                .orElseThrow(() -> new IllegalArgumentException("Impianto non trovato: " + impiantoId));

        Intervento i = new Intervento();
        i.setImpianto(impianto);
        i.setTipo(TipoIntervento.valueOf(tipo));
        i.setStato(StatoIntervento.valueOf(stato));
        i.setDescrizione(descrizione);
        i.setNoteTecnico(noteTecnico);
        i.setCosto(costo);
        i.setDataProgrammata(parseDate(dataProgrammataStr));
        i.setDataEsecuzione(parseDate(dataEsecuzioneStr));
        i.setInizioIntervento(parseTime(inizioStr));
        i.setFineIntervento(parseTime(fineStr));

        interventoRepository.save(i);
        return "redirect:/impianti/" + impiantoId;
    }

    @GetMapping("/interventi/{id}/modifica")
    public String mostraFormModifica(@PathVariable Long id, Model model) {
        Intervento i = interventoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Intervento non trovato: " + id));

        model.addAttribute("impianto", i.getImpianto());
        model.addAttribute("intervento", i);
        model.addAttribute("tipiIntervento", TipoIntervento.values());
        model.addAttribute("statiIntervento", StatoIntervento.values());
        model.addAttribute("mode", "edit");
        model.addAttribute("formAction", "/interventi/" + id + "/modifica");
        return "intervento-form";
    }

    @PostMapping("/interventi/{id}/modifica")
    public String salvaModifica(@PathVariable Long id,
            @RequestParam("tipo") String tipo,
            @RequestParam("stato") String stato,
            @RequestParam("descrizione") String descrizione,
            @RequestParam(value = "dataProgrammata", required = false) String dataProgrammataStr,
            @RequestParam(value = "dataEsecuzione",  required = false) String dataEsecuzioneStr,
            @RequestParam(value = "inizioIntervento", required = false) String inizioStr,
            @RequestParam(value = "fineIntervento",   required = false) String fineStr,
            @RequestParam(value = "noteTecnico", required = false) String noteTecnico,
            @RequestParam(value = "costo", required = false) BigDecimal costo) {

        Intervento i = interventoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Intervento non trovato: " + id));

        i.setTipo(TipoIntervento.valueOf(tipo));
        i.setStato(StatoIntervento.valueOf(stato));
        i.setDescrizione(descrizione);
        i.setNoteTecnico(noteTecnico);
        i.setCosto(costo);
        i.setDataProgrammata(parseDate(dataProgrammataStr));
        i.setDataEsecuzione(parseDate(dataEsecuzioneStr));
        i.setInizioIntervento(parseTime(inizioStr));
        i.setFineIntervento(parseTime(fineStr));

        interventoRepository.save(i);
        return "redirect:/impianti/" + i.getImpianto().getId();
    }

    @PostMapping("/interventi/{id}/elimina")
    public String elimina(@PathVariable Long id) {
        Intervento i = interventoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Intervento non trovato: " + id));
        Long impiantoId = i.getImpianto().getId();
        interventoRepository.delete(i);
        return "redirect:/impianti/" + impiantoId;
    }

    // ── helper ──────────────────────────────────────────────
    private LocalDate parseDate(String v) {
        return (v == null || v.isBlank()) ? null : LocalDate.parse(v);
    }

    private LocalTime parseTime(String v) {
        return (v == null || v.isBlank()) ? null : LocalTime.parse(v);
    }
}
