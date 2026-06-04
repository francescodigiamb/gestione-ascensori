package com.francesco.gestione_ascensori.controller;

import com.francesco.gestione_ascensori.model.*;
import com.francesco.gestione_ascensori.repository.ImpiantoRepository;
import com.francesco.gestione_ascensori.repository.InterventoRepository;
import com.francesco.gestione_ascensori.repository.UtenteRepository;
import org.springframework.security.core.Authentication;
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
    private final UtenteRepository utenteRepository;

    public InterventoController(ImpiantoRepository impiantoRepository,
            InterventoRepository interventoRepository,
            UtenteRepository utenteRepository) {
        this.impiantoRepository = impiantoRepository;
        this.interventoRepository = interventoRepository;
        this.utenteRepository = utenteRepository;
    }

    @GetMapping("/impianti/{impiantoId}/interventi/nuovo")
    public String mostraFormNuovo(@PathVariable Long impiantoId, Model model) {
        Impianto impianto = impiantoRepository.findById(impiantoId)
                .orElseThrow(() -> new IllegalArgumentException("Impianto non trovato: " + impiantoId));
        model.addAttribute("impianto", impianto);
        model.addAttribute("tipiIntervento", TipoIntervento.values());
        model.addAttribute("statiIntervento", StatoIntervento.values());
        model.addAttribute("operatori", utenteRepository.findByRuolo(RuoloUtente.OPERATORE));
        model.addAttribute("mode", "create");
        model.addAttribute("formAction", "/impianti/" + impiantoId + "/interventi/nuovo");
        return "intervento-form";
    }

    @PostMapping("/impianti/{impiantoId}/interventi/nuovo")
    public String salvaNuovo(@PathVariable Long impiantoId,
            @RequestParam("tipo") String tipo,
            @RequestParam("stato") String stato,
            @RequestParam("descrizione") String descrizione,
            @RequestParam(value = "dataProgrammata",  required = false) String dataProgrammataStr,
            @RequestParam(value = "dataEsecuzione",   required = false) String dataEsecuzioneStr,
            @RequestParam(value = "inizioIntervento", required = false) String inizioStr,
            @RequestParam(value = "fineIntervento",   required = false) String fineStr,
            @RequestParam(value = "noteTecnico",      required = false) String noteTecnico,
            @RequestParam(value = "costo",            required = false) BigDecimal costo,
            @RequestParam(value = "assegnatarioId",   required = false) Long assegnatarioId) {

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
        if (assegnatarioId != null) {
            utenteRepository.findById(assegnatarioId).ifPresent(i::setAssegnatario);
        }

        interventoRepository.save(i);
        return "redirect:/impianti/" + impiantoId;
    }

    @GetMapping("/interventi/{id}/modifica")
    public String mostraFormModifica(@PathVariable Long id,
            @RequestParam(value = "errore", required = false) String errore,
            Model model, Authentication auth) {
        Intervento i = interventoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Intervento non trovato: " + id));

        // Operatore: può aprire la scheda solo se è l'assegnatario
        if (!isAdmin(auth)) {
            Utente corrente = utenteRepository.findByUsername(auth.getName()).orElseThrow();
            if (i.getAssegnatario() == null || !i.getAssegnatario().getId().equals(corrente.getId())) {
                return "redirect:/impianti/" + i.getImpianto().getId();
            }
        }

        model.addAttribute("impianto", i.getImpianto());
        model.addAttribute("intervento", i);
        model.addAttribute("tipiIntervento", TipoIntervento.values());
        model.addAttribute("statiIntervento", StatoIntervento.values());
        model.addAttribute("operatori", utenteRepository.findByRuolo(RuoloUtente.OPERATORE));
        model.addAttribute("mode", "edit");
        model.addAttribute("formAction", "/interventi/" + id + "/modifica");
        if ("rapportino".equals(errore)) {
            model.addAttribute("errore", "Per segnare l'intervento come Completato è necessario compilare il Rapportino.");
        }
        return "intervento-form";
    }

    @PostMapping("/interventi/{id}/modifica")
    public String salvaModifica(@PathVariable Long id, Authentication auth,
            @RequestParam("tipo") String tipo,
            @RequestParam("stato") String stato,
            @RequestParam("descrizione") String descrizione,
            @RequestParam(value = "dataProgrammata",  required = false) String dataProgrammataStr,
            @RequestParam(value = "dataEsecuzione",   required = false) String dataEsecuzioneStr,
            @RequestParam(value = "inizioIntervento", required = false) String inizioStr,
            @RequestParam(value = "fineIntervento",   required = false) String fineStr,
            @RequestParam(value = "noteTecnico",      required = false) String noteTecnico,
            @RequestParam(value = "costo",            required = false) BigDecimal costo,
            @RequestParam(value = "assegnatarioId",   required = false) Long assegnatarioId) {

        Intervento i = interventoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Intervento non trovato: " + id));

        // Validazione: operatore non può completare senza rapportino
        if (!isAdmin(auth)) {
            boolean vuoleCompletare = StatoIntervento.COMPLETATO.name().equals(stato);
            boolean rapportinoVuoto = noteTecnico == null || noteTecnico.isBlank();
            if (vuoleCompletare && rapportinoVuoto) {
                return "redirect:/interventi/" + id + "/modifica?errore=rapportino";
            }
        }

        if (isAdmin(auth)) {
            // Admin: aggiorna tutto
            i.setTipo(TipoIntervento.valueOf(tipo));
            i.setStato(StatoIntervento.valueOf(stato));
            i.setDescrizione(descrizione);
            i.setDataProgrammata(parseDate(dataProgrammataStr));
            i.setDataEsecuzione(parseDate(dataEsecuzioneStr));
            i.setInizioIntervento(parseTime(inizioStr));
            i.setFineIntervento(parseTime(fineStr));
            i.setNoteTecnico(noteTecnico);
            i.setCosto(costo);
            if (assegnatarioId != null) {
                utenteRepository.findById(assegnatarioId).ifPresent(i::setAssegnatario);
            } else {
                i.setAssegnatario(null);
            }
        } else {
            // Operatore: aggiorna solo i campi di sua competenza
            Utente corrente = utenteRepository.findByUsername(auth.getName()).orElseThrow();
            if (i.getAssegnatario() == null || !i.getAssegnatario().getId().equals(corrente.getId())) {
                return "redirect:/impianti/" + i.getImpianto().getId();
            }
            i.setStato(StatoIntervento.valueOf(stato));
            i.setInizioIntervento(parseTime(inizioStr));
            i.setFineIntervento(parseTime(fineStr));
            i.setNoteTecnico(noteTecnico);
        }

        interventoRepository.save(i);
        return "redirect:/impianti/" + i.getImpianto().getId();
    }

    // Cambio stato rapido dalla card — operatore assegnato o admin
    @PostMapping("/interventi/{id}/stato")
    public String cambiaStato(@PathVariable Long id,
            @RequestParam("stato") String stato,
            Authentication auth) {
        Intervento i = interventoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Intervento non trovato: " + id));

        // Controllo: operatore può cambiare stato solo se è l'assegnatario
        if (!isAdmin(auth)) {
            Utente corrente = utenteRepository.findByUsername(auth.getName()).orElseThrow();
            if (i.getAssegnatario() == null || !i.getAssegnatario().getId().equals(corrente.getId())) {
                return "redirect:/";
            }
        }

        i.setStato(StatoIntervento.valueOf(stato));
        interventoRepository.save(i);

        // Torna alla pagina da cui è arrivato
        String referer = "/impianti/" + i.getImpianto().getId();
        return "redirect:" + referer;
    }

    // Assegnazione rapida dalla card — solo admin
    @PostMapping("/interventi/{id}/assegna")
    public String assegna(@PathVariable Long id,
            @RequestParam(value = "assegnatarioId", required = false) Long assegnatarioId) {
        Intervento i = interventoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Intervento non trovato: " + id));
        if (assegnatarioId != null) {
            utenteRepository.findById(assegnatarioId).ifPresent(i::setAssegnatario);
        } else {
            i.setAssegnatario(null);
        }
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

    // ── helper ──────────────────────────────────────
    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private LocalDate parseDate(String v) {
        return (v == null || v.isBlank()) ? null : LocalDate.parse(v);
    }

    private LocalTime parseTime(String v) {
        return (v == null || v.isBlank()) ? null : LocalTime.parse(v);
    }
}
