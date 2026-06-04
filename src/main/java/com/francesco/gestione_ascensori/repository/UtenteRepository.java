package com.francesco.gestione_ascensori.repository;

import com.francesco.gestione_ascensori.model.RuoloUtente;
import com.francesco.gestione_ascensori.model.Utente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UtenteRepository extends JpaRepository<Utente, Long> {

    Optional<Utente> findByUsername(String username);

    List<Utente> findByRuolo(RuoloUtente ruolo);
}
