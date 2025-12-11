package com.francesco.gestione_ascensori.controller;

import com.francesco.gestione_ascensori.model.*;
import com.francesco.gestione_ascensori.repository.ImpiantoRepository;
import com.francesco.gestione_ascensori.repository.InterventoRepository;
import com.francesco.gestione_ascensori.repository.LuogoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ✅ Controller per la visualizzazione di luoghi e impianti.
 */
@Controller
public class LuogoController {

    private final LuogoRepository luogoRepository;
    private final ImpiantoRepository impiantoRepository;
    private final InterventoRepository interventoRepository;

    public LuogoController(LuogoRepository luogoRepository,
            ImpiantoRepository impiantoRepository,
            InterventoRepository interventoRepository) {
        this.luogoRepository = luogoRepository;
        this.impiantoRepository = impiantoRepository;
        this.interventoRepository = interventoRepository;
    }

    @GetMapping("/luoghi/{id}")
    public String impiantiPerLuogo(@PathVariable Long id,
            @RequestParam(name = "q", required = false) String query,
            Model model) {

        // Recupero il luogo o errore
        Luogo luogo = luogoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Luogo non trovato: " + id));

        // Tutti gli impianti del luogo
        List<Impianto> impianti = impiantoRepository.findByLuogo(luogo);

        // Se c'è una query di ricerca, filtro in memoria per nome/indirizzo (senza
        // lambda)
        List<Impianto> impiantiFiltrati = new java.util.ArrayList<>();

        if (query != null && !query.trim().isEmpty()) {
            String qLower = query.toLowerCase();

            for (Impianto imp : impianti) {
                String nome = imp.getNome() != null ? imp.getNome().toLowerCase() : "";
                String indirizzo = imp.getIndirizzo() != null ? imp.getIndirizzo().toLowerCase() : "";

                if (nome.contains(qLower) || indirizzo.contains(qLower)) {
                    impiantiFiltrati.add(imp);
                }
            }
        } else {
            // Nessuna ricerca: uso la lista completa
            impiantiFiltrati = impianti;
        }

        // Creo la lista di riepiloghi (impianto + conteggi interventi) SENZA lambda
        List<ImpiantoRiepilogo> impiantiRiepilogo = new java.util.ArrayList<>();

        for (Impianto impianto : impiantiFiltrati) {
            long completati = interventoRepository.countByImpiantoAndStato(
                    impianto, StatoIntervento.COMPLETATO);
            long daFare = interventoRepository.countByImpiantoAndStato(
                    impianto, StatoIntervento.DA_FARE);

            ImpiantoRiepilogo riepilogo = new ImpiantoRiepilogo(impianto, completati, daFare);
            impiantiRiepilogo.add(riepilogo);
        }

        model.addAttribute("luogo", luogo);
        model.addAttribute("impiantiRiepilogo", impiantiRiepilogo);
        model.addAttribute("pageTitle", "Impianti - " + luogo.getNome());
        model.addAttribute("query", query); // per rimettere il testo nel campo di ricerca

        return "luogo-impianti";
    }

    /**
     * ✅ Classe di appoggio per portare in pagina
     * impianto + conteggi interventi.
     */
    public static class ImpiantoRiepilogo {
        private final Impianto impianto;
        private final long interventiCompletati;
        private final long interventiDaFare;

        public ImpiantoRiepilogo(Impianto impianto, long interventiCompletati, long interventiDaFare) {
            this.impianto = impianto;
            this.interventiCompletati = interventiCompletati;
            this.interventiDaFare = interventiDaFare;
        }

        public Impianto getImpianto() {
            return impianto;
        }

        public long getInterventiCompletati() {
            return interventiCompletati;
        }

        public long getInterventiDaFare() {
            return interventiDaFare;
        }
    }

    // ✅ FORM NUOVO LUOGO
    @GetMapping("/luoghi/nuovo")
    public String mostraFormNuovoLuogo(Model model) {

        Luogo luogo = new Luogo();
        model.addAttribute("luogo", luogo);
        model.addAttribute("pageTitle", "Nuovo luogo");

        return "luogo-form";
    }

    // ✅ SALVA NUOVO LUOGO
    @PostMapping("/luoghi/nuovo")
    public String salvaNuovoLuogo(@RequestParam("nome") String nome,
            @RequestParam(value = "descrizione", required = false) String descrizione) {

        Luogo luogo = new Luogo();
        luogo.setNome(nome);
        luogo.setDescrizione(descrizione);

        luogoRepository.save(luogo);

        // Torniamo alla homepage con la lista luoghi
        return "redirect:/";
    }

}
