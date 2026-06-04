package com.francesco.gestione_ascensori.controller;

import com.francesco.gestione_ascensori.model.*;
import com.francesco.gestione_ascensori.repository.ImpiantoRepository;
import com.francesco.gestione_ascensori.repository.InterventoRepository;
import com.francesco.gestione_ascensori.repository.LuogoRepository;
import com.francesco.gestione_ascensori.repository.UtenteRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class ImpiantoController {

    private final ImpiantoRepository impiantoRepository;
    private final InterventoRepository interventoRepository;
    private final LuogoRepository luogoRepository;
    private final UtenteRepository utenteRepository;

    public ImpiantoController(ImpiantoRepository impiantoRepository,
            InterventoRepository interventoRepository, LuogoRepository luogoRepository,
            UtenteRepository utenteRepository) {
        this.impiantoRepository = impiantoRepository;
        this.interventoRepository = interventoRepository;
        this.luogoRepository = luogoRepository;
        this.utenteRepository = utenteRepository;
    }

    @GetMapping("/impianti/{id}")
    public String dettaglio(@PathVariable Long id, Model model) {
        Impianto impianto = impiantoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Impianto non trovato: " + id));

        List<Intervento> interventi = interventoRepository.findByImpianto(impianto);

        BigDecimal totaleCosti = BigDecimal.ZERO;
        for (Intervento i : interventi) {
            if (i.getStato() == StatoIntervento.COMPLETATO && i.getCosto() != null) {
                totaleCosti = totaleCosti.add(i.getCosto());
            }
        }

        model.addAttribute("impianto", impianto);
        model.addAttribute("interventi", interventi);
        model.addAttribute("totaleCosti", totaleCosti);
        model.addAttribute("operatori", utenteRepository.findByRuolo(RuoloUtente.OPERATORE));
        model.addAttribute("pageTitle", impianto.getNome());
        return "impianto-dettaglio";
    }

    @GetMapping("/luoghi/{luogoId}/impianti/nuovo")
    public String formNuovo(@PathVariable Long luogoId, Model model) {
        Luogo luogo = luogoRepository.findById(luogoId)
                .orElseThrow(() -> new IllegalArgumentException("Luogo non trovato: " + luogoId));
        Impianto impianto = new Impianto();
        impianto.setLuogo(luogo);
        model.addAttribute("luogo", luogo);
        model.addAttribute("impianto", impianto);
        model.addAttribute("statiImpianto", StatoImpianto.values());
        model.addAttribute("mode", "create");
        model.addAttribute("formAction", "/luoghi/" + luogoId + "/impianti/nuovo");
        model.addAttribute("pageTitle", "Nuovo impianto");
        return "impianto-form";
    }

    @PostMapping("/luoghi/{luogoId}/impianti/nuovo")
    public String salvaNuovo(@PathVariable Long luogoId,
            @RequestParam("nome") String nome,
            @RequestParam(value = "matricola", required = false) String matricola,
            @RequestParam("indirizzo") String indirizzo,
            @RequestParam("stato") String stato,
            @RequestParam(value = "note", required = false) String note) {

        Luogo luogo = luogoRepository.findById(luogoId)
                .orElseThrow(() -> new IllegalArgumentException("Luogo non trovato: " + luogoId));
        Impianto impianto = new Impianto();
        impianto.setNome(nome);
        impianto.setMatricola(matricola);
        impianto.setIndirizzo(indirizzo);
        impianto.setLuogo(luogo);
        impianto.setStato(StatoImpianto.valueOf(stato));
        impianto.setNote(note);
        impiantoRepository.save(impianto);
        return "redirect:/luoghi/" + luogoId;
    }

    @GetMapping("/impianti/{id}/modifica")
    public String formModifica(@PathVariable Long id, Model model) {
        Impianto impianto = impiantoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Impianto non trovato: " + id));
        model.addAttribute("luogo", impianto.getLuogo());
        model.addAttribute("impianto", impianto);
        model.addAttribute("statiImpianto", StatoImpianto.values());
        model.addAttribute("mode", "edit");
        model.addAttribute("formAction", "/impianti/" + id + "/modifica");
        model.addAttribute("pageTitle", "Modifica impianto");
        return "impianto-form";
    }

    @PostMapping("/impianti/{id}/modifica")
    public String salvaModifica(@PathVariable Long id,
            @RequestParam("nome") String nome,
            @RequestParam(value = "matricola", required = false) String matricola,
            @RequestParam("indirizzo") String indirizzo,
            @RequestParam("stato") String stato,
            @RequestParam(value = "note", required = false) String note) {

        Impianto impianto = impiantoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Impianto non trovato: " + id));
        impianto.setNome(nome);
        impianto.setMatricola(matricola);
        impianto.setIndirizzo(indirizzo);
        impianto.setStato(StatoImpianto.valueOf(stato));
        impianto.setNote(note);
        impiantoRepository.save(impianto);
        return "redirect:/luoghi/" + impianto.getLuogo().getId();
    }

    @PostMapping("/impianti/{id}/elimina")
    public String elimina(@PathVariable Long id) {
        Impianto impianto = impiantoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Impianto non trovato: " + id));
        Long luogoId = impianto.getLuogo().getId();
        interventoRepository.deleteAll(interventoRepository.findByImpianto(impianto));
        impiantoRepository.delete(impianto);
        return "redirect:/luoghi/" + luogoId;
    }
}
