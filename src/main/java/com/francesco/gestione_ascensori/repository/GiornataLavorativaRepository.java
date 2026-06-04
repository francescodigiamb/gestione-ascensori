package com.francesco.gestione_ascensori.repository;

import com.francesco.gestione_ascensori.model.GiornataLavorativa;
import com.francesco.gestione_ascensori.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface GiornataLavorativaRepository extends JpaRepository<GiornataLavorativa, Long> {

    Optional<GiornataLavorativa> findByUtenteAndData(Utente utente, LocalDate data);

    // Tutte le giornate in un intervallo di date (per riepilogo mensile)
    List<GiornataLavorativa> findByDataBetweenOrderByDataAscUtenteAsc(
            LocalDate from, LocalDate to);
}
