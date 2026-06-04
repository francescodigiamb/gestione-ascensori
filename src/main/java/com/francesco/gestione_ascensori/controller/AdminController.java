package com.francesco.gestione_ascensori.controller;

import com.francesco.gestione_ascensori.model.GiornataLavorativa;
import com.francesco.gestione_ascensori.model.RuoloUtente;
import com.francesco.gestione_ascensori.model.Utente;
import com.francesco.gestione_ascensori.repository.GiornataLavorativaRepository;
import com.francesco.gestione_ascensori.repository.UtenteRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UtenteRepository utenteRepository;
    private final PasswordEncoder passwordEncoder;
    private final GiornataLavorativaRepository giornataRepository;

    public AdminController(UtenteRepository utenteRepository,
                           PasswordEncoder passwordEncoder,
                           GiornataLavorativaRepository giornataRepository) {
        this.utenteRepository = utenteRepository;
        this.passwordEncoder = passwordEncoder;
        this.giornataRepository = giornataRepository;
    }

    // ── Utenti ──────────────────────────────────────────────
    @GetMapping
    public String admin(Model model) {
        model.addAttribute("utenti", utenteRepository.findAll());
        return "admin/utenti";
    }

    @PostMapping("/utenti/{id}/password")
    public String resetPassword(@PathVariable Long id,
            @RequestParam("nuovaPassword") String nuovaPassword, RedirectAttributes ra) {
        Utente u = utenteRepository.findById(id).orElseThrow();
        u.setPassword(passwordEncoder.encode(nuovaPassword));
        utenteRepository.save(u);
        ra.addFlashAttribute("msg", "Password aggiornata per " + u.getNomeCompleto());
        return "redirect:/admin";
    }

    @PostMapping("/utenti/{id}/nome")
    public String aggiornaNome(@PathVariable Long id,
            @RequestParam("nomeCompleto") String nomeCompleto, RedirectAttributes ra) {
        Utente u = utenteRepository.findById(id).orElseThrow();
        u.setNomeCompleto(nomeCompleto);
        utenteRepository.save(u);
        ra.addFlashAttribute("msg", "Nome aggiornato.");
        return "redirect:/admin";
    }

    // ── Giornate lavorative ─────────────────────────────────
    @GetMapping("/giornate")
    public String giornate(
            @RequestParam(value = "mese", required = false) Integer mese,
            @RequestParam(value = "anno", required = false) Integer anno,
            Model model) {

        YearMonth meseSelezionato = (mese != null && anno != null)
                ? YearMonth.of(anno, mese)
                : YearMonth.now();

        LocalDate from = meseSelezionato.atDay(1);
        LocalDate to   = meseSelezionato.atEndOfMonth();

        List<GiornataLavorativa> giornate =
                giornataRepository.findByDataBetweenOrderByDataAscUtenteAsc(from, to);

        // Raggruppo per operatore
        List<Utente> operatori = utenteRepository.findByRuolo(RuoloUtente.OPERATORE);
        Map<Utente, List<GiornataLavorativa>> perOperatore = new LinkedHashMap<>();
        Map<Utente, Long> totaleMinutiPerOperatore = new LinkedHashMap<>();

        for (Utente op : operatori) {
            List<GiornataLavorativa> sue = new ArrayList<>();
            long totaleMinuti = 0;
            for (GiornataLavorativa g : giornate) {
                if (g.getUtente().getId().equals(op.getId())) {
                    sue.add(g);
                    totaleMinuti += g.getMinutiLavorati();
                }
            }
            perOperatore.put(op, sue);
            totaleMinutiPerOperatore.put(op, totaleMinuti);
        }

        model.addAttribute("meseSelezionato", meseSelezionato);
        model.addAttribute("mesePrecedente",  meseSelezionato.minusMonths(1));
        model.addAttribute("meseSuccessivo",  meseSelezionato.plusMonths(1));
        model.addAttribute("perOperatore", perOperatore);
        model.addAttribute("totaleMinutiPerOperatore", totaleMinutiPerOperatore);
        return "admin/giornate";
    }

    // Admin modifica una giornata
    @PostMapping("/giornate/{id}/modifica")
    public String modificaGiornata(@PathVariable Long id,
            @RequestParam(value = "oraInizio", required = false) String inizioStr,
            @RequestParam(value = "oraFine",   required = false) String fineStr,
            RedirectAttributes ra) {

        GiornataLavorativa g = giornataRepository.findById(id).orElseThrow();
        if (inizioStr != null && !inizioStr.isBlank()) g.setOraInizio(LocalTime.parse(inizioStr));
        if (fineStr   != null && !fineStr.isBlank())   g.setOraFine(LocalTime.parse(fineStr));
        // L'admin può sbloccarla se necessario
        if (g.getOraInizio() != null && g.getOraFine() != null) g.setBloccata(true);
        giornataRepository.save(g);

        ra.addFlashAttribute("msg", "Giornata aggiornata.");
        // Torna al mese della giornata
        return "redirect:/admin/giornate?mese=" + g.getData().getMonthValue()
               + "&anno=" + g.getData().getYear();
    }
}
