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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class InterventoController {

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private final ImpiantoRepository impiantoRepository;
    private final InterventoRepository interventoRepository;

    public InterventoController(ImpiantoRepository impiantoRepository,
            InterventoRepository interventoRepository) {
        this.impiantoRepository = impiantoRepository;
        this.interventoRepository = interventoRepository;
    }

    @GetMapping("/impianti/{impiantoId}/interventi/nuovo")
    public String mostraFormNuovoIntervento(@PathVariable Long impiantoId, Model model) {
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
    public String salvaNuovoIntervento(@PathVariable Long impiantoId,
            @RequestParam("tipo") String tipo,
            @RequestParam("stato") String stato,
            @RequestParam("descrizione") String descrizione,
            @RequestParam(value = "dataProgrammata", required = false) String dataProgrammataStr,
            @RequestParam(value = "dataEsecuzione", required = false) String dataEsecuzioneStr,
            @RequestParam(value = "noteTecnico", required = false) String noteTecnico,
            @RequestParam(value = "costo", required = false) BigDecimal costo) {

        Impianto impianto = impiantoRepository.findById(impiantoId)
                .orElseThrow(() -> new IllegalArgumentException("Impianto non trovato: " + impiantoId));

        Intervento intervento = new Intervento();
        intervento.setImpianto(impianto);
        intervento.setTipo(TipoIntervento.valueOf(tipo));
        intervento.setStato(StatoIntervento.valueOf(stato));
        intervento.setDescrizione(descrizione);
        intervento.setNoteTecnico(noteTecnico);
        intervento.setCosto(costo);
        intervento.setDataProgrammata(parseDateTime(dataProgrammataStr));
        intervento.setDataEsecuzione(parseDateTime(dataEsecuzioneStr));

        interventoRepository.save(intervento);
        return "redirect:/impianti/" + impiantoId;
    }

    @GetMapping("/interventi/{id}/modifica")
    public String mostraFormModificaIntervento(@PathVariable Long id, Model model) {
        Intervento intervento = interventoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Intervento non trovato: " + id));

        model.addAttribute("impianto", intervento.getImpianto());
        model.addAttribute("intervento", intervento);
        model.addAttribute("tipiIntervento", TipoIntervento.values());
        model.addAttribute("statiIntervento", StatoIntervento.values());
        model.addAttribute("mode", "edit");
        model.addAttribute("formAction", "/interventi/" + id + "/modifica");

        // Valori formattati per i campi datetime-local
        if (intervento.getDataProgrammata() != null) {
            model.addAttribute("dataProgrammataFormatted", intervento.getDataProgrammata().format(DT_FMT));
        }
        if (intervento.getDataEsecuzione() != null) {
            model.addAttribute("dataEsecuzioneFormatted", intervento.getDataEsecuzione().format(DT_FMT));
        }

        return "intervento-form";
    }

    @PostMapping("/interventi/{id}/modifica")
    public String salvaModificaIntervento(@PathVariable Long id,
            @RequestParam("tipo") String tipo,
            @RequestParam("stato") String stato,
            @RequestParam("descrizione") String descrizione,
            @RequestParam(value = "dataProgrammata", required = false) String dataProgrammataStr,
            @RequestParam(value = "dataEsecuzione", required = false) String dataEsecuzioneStr,
            @RequestParam(value = "noteTecnico", required = false) String noteTecnico,
            @RequestParam(value = "costo", required = false) BigDecimal costo) {

        Intervento intervento = interventoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Intervento non trovato: " + id));

        intervento.setTipo(TipoIntervento.valueOf(tipo));
        intervento.setStato(StatoIntervento.valueOf(stato));
        intervento.setDescrizione(descrizione);
        intervento.setNoteTecnico(noteTecnico);
        intervento.setCosto(costo);
        intervento.setDataProgrammata(parseDateTime(dataProgrammataStr));
        intervento.setDataEsecuzione(parseDateTime(dataEsecuzioneStr));

        interventoRepository.save(intervento);
        return "redirect:/impianti/" + intervento.getImpianto().getId();
    }

    @PostMapping("/interventi/{id}/elimina")
    public String eliminaIntervento(@PathVariable Long id) {
        Intervento intervento = interventoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Intervento non trovato: " + id));
        Long impiantoId = intervento.getImpianto().getId();
        interventoRepository.delete(intervento);
        return "redirect:/impianti/" + impiantoId;
    }

    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.isBlank()) return null;
        return LocalDateTime.parse(value, DT_FMT);
    }
}
