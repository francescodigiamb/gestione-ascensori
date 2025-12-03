package com.francesco.gestione_ascensori.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.francesco.gestione_ascensori.model.Impianto;
import com.francesco.gestione_ascensori.model.Intervento;
import com.francesco.gestione_ascensori.model.StatoIntervento;

import java.util.List;

/**
 * ✅ Repository per Intervento
 * Gestisce la lettura/scrittura degli interventi di manutenzione.
 */
public interface InterventoRepository extends JpaRepository<Intervento, Long> {

    // Tutti gli interventi per un certo impianto
    List<Intervento> findByImpianto(Impianto impianto);

    // Interventi filtrati per impianto e stato (es. tutti i DA_FARE)
    List<Intervento> findByImpiantoAndStato(Impianto impianto, StatoIntervento stato);

    // ✅ Conteggio interventi per stato (ci serve per la mini-didascalia)
    long countByImpiantoAndStato(Impianto impianto, StatoIntervento stato);

}