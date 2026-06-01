package com.francesco.gestione_ascensori.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.francesco.gestione_ascensori.model.Impianto;
import com.francesco.gestione_ascensori.model.Intervento;
import com.francesco.gestione_ascensori.model.StatoIntervento;

import java.time.LocalDateTime;
import java.util.List;

public interface InterventoRepository extends JpaRepository<Intervento, Long> {

    List<Intervento> findByImpianto(Impianto impianto);

    List<Intervento> findByImpiantoAndStato(Impianto impianto, StatoIntervento stato);

    long countByImpiantoAndStato(Impianto impianto, StatoIntervento stato);

    long countByStato(StatoIntervento stato);

    @Query("SELECT i FROM Intervento i WHERE i.stato = :stato AND i.dataProgrammata IS NOT NULL AND i.dataProgrammata <= :limite ORDER BY i.dataProgrammata ASC")
    List<Intervento> findInScadenzaEntro(@Param("stato") StatoIntervento stato, @Param("limite") LocalDateTime limite);

    @Query("SELECT COUNT(i) FROM Intervento i WHERE i.stato = :stato AND i.dataProgrammata IS NOT NULL AND i.dataProgrammata <= :limite")
    long countInScadenzaEntro(@Param("stato") StatoIntervento stato, @Param("limite") LocalDateTime limite);
}
