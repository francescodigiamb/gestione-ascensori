package com.francesco.gestione_ascensori.config;

import com.francesco.gestione_ascensori.model.*;
import com.francesco.gestione_ascensori.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class DataLoader implements CommandLineRunner {

    private final LuogoRepository luogoRepository;
    private final ImpiantoRepository impiantoRepository;
    private final InterventoRepository interventoRepository;
    private final UtenteRepository utenteRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(LuogoRepository luogoRepository,
            ImpiantoRepository impiantoRepository,
            InterventoRepository interventoRepository,
            UtenteRepository utenteRepository,
            PasswordEncoder passwordEncoder) {
        this.luogoRepository = luogoRepository;
        this.impiantoRepository = impiantoRepository;
        this.interventoRepository = interventoRepository;
        this.utenteRepository = utenteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Utenti — inseriti solo se non esistono già
        if (utenteRepository.count() == 0) {
            // Admin — password: Ascensori2024!
            utenteRepository.save(new Utente("admin",
                    passwordEncoder.encode("Ascensori2024!"),
                    "Amministratore", RuoloUtente.ADMIN));

            // 4 Operatori — password iniziale: Op2024! (da cambiare)
            utenteRepository.save(new Utente("op1",
                    passwordEncoder.encode("Op2024!"), "Operatore 1", RuoloUtente.OPERATORE));
            utenteRepository.save(new Utente("op2",
                    passwordEncoder.encode("Op2024!"), "Operatore 2", RuoloUtente.OPERATORE));
            utenteRepository.save(new Utente("op3",
                    passwordEncoder.encode("Op2024!"), "Operatore 3", RuoloUtente.OPERATORE));
            utenteRepository.save(new Utente("op4",
                    passwordEncoder.encode("Op2024!"), "Operatore 4", RuoloUtente.OPERATORE));
        }

        // Luoghi e impianti — inseriti solo se non esistono già
        if (luogoRepository.count() > 0) return;

        Luogo pescara = new Luogo("Pescara", "Zona Pescara e dintorni");
        Luogo chieti  = new Luogo("Chieti",  "Zona Chieti e dintorni");
        luogoRepository.save(pescara);
        luogoRepository.save(chieti);

        Impianto imp1 = new Impianto("ASC-PE-001", "Via Roma 10, Pescara", pescara, StatoImpianto.ATTIVO, "Cond. 'Roma 10', scala A");
        imp1.setMatricola("MAT-001-PE");
        Impianto imp2 = new Impianto("ASC-PE-002", "Via Milano 25, Pescara", pescara, StatoImpianto.ATTIVO, "Palazzo uffici");
        imp2.setMatricola("MAT-002-PE");
        Impianto imp3 = new Impianto("ASC-CH-001", "Via Trento 5, Chieti", chieti, StatoImpianto.ATTIVO, "Condominio 6 piani");
        imp3.setMatricola("MAT-001-CH");
        impiantoRepository.save(imp1);
        impiantoRepository.save(imp2);
        impiantoRepository.save(imp3);

        Intervento i1 = new Intervento(imp1, TipoIntervento.MANUTENZIONE, StatoIntervento.COMPLETATO,
                LocalDate.now().minusDays(10), "Manutenzione periodica ascensore");
        i1.setDataEsecuzione(LocalDate.now().minusDays(10));
        i1.setCosto(new BigDecimal("120.00"));
        i1.setNoteTecnico("Lubrificazione guide, controllo freni, test corsa completa.");

        Intervento i2 = new Intervento(imp1, TipoIntervento.GUASTO, StatoIntervento.DA_FARE,
                LocalDate.now().plusDays(2), "Segnalato rumore anomalo in cabina");

        Intervento i3 = new Intervento(imp2, TipoIntervento.VERIFICA, StatoIntervento.COMPLETATO,
                LocalDate.now().minusDays(3), "Verifica generale impianto");
        i3.setDataEsecuzione(LocalDate.now().minusDays(3));
        i3.setCosto(new BigDecimal("150.00"));

        Intervento i4 = new Intervento(imp3, TipoIntervento.MANUTENZIONE, StatoIntervento.DA_FARE,
                LocalDate.now().plusDays(5), "Manutenzione periodica ascensore");

        interventoRepository.save(i1);
        interventoRepository.save(i2);
        interventoRepository.save(i3);
        interventoRepository.save(i4);
    }
}
