package com.francesco.gestione_ascensori.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.francesco.gestione_ascensori.model.Impianto;
import com.francesco.gestione_ascensori.model.Luogo;
import com.francesco.gestione_ascensori.model.StatoImpianto;

import java.util.List;

public interface ImpiantoRepository extends JpaRepository<Impianto, Long> {

    List<Impianto> findByLuogo(Luogo luogo);

    List<Impianto> findByNomeContainingIgnoreCaseOrIndirizzoContainingIgnoreCase(
            String nome, String indirizzo);

    long countByStato(StatoImpianto stato);

    List<Impianto> findByStato(StatoImpianto stato);
}