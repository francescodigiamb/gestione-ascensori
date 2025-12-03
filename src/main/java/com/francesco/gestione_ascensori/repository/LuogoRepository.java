package com.francesco.gestione_ascensori.repository;

import com.francesco.gestione_ascensori.model.Luogo;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * ✅ Repository per Luogo
 * Permette di eseguire operazioni CRUD sui luoghi (Pescara, Chieti, ecc.).
 */
public interface LuogoRepository extends JpaRepository<Luogo, Long> {

    // Esempio di metodo custom (in futuro potrà tornare utile)
    Luogo findByNomeIgnoreCase(String nome);
}