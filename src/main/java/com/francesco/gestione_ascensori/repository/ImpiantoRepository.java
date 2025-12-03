package com.francesco.gestione_ascensori.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.francesco.gestione_ascensori.model.Impianto;
import com.francesco.gestione_ascensori.model.Luogo;

import java.util.List;

/**
 * ✅ Repository per Impianto
 * Operazioni CRUD sugli impianti e ricerche per luogo o testo.
 */
public interface ImpiantoRepository extends JpaRepository<Impianto, Long> {

    // Lista impianti per un certo luogo
    List<Impianto> findByLuogo(Luogo luogo);

    // Ricerca impianti per nome o indirizzo (case insensitive, contiene)
    List<Impianto> findByNomeContainingIgnoreCaseOrIndirizzoContainingIgnoreCase(
            String nome, String indirizzo);
}