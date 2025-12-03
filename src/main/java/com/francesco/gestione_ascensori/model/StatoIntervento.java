package com.francesco.gestione_ascensori.model;

/**
 * ✅ Stato dell'intervento di manutenzione.
 * - DA_FARE: intervento pianificato ma non iniziato
 * - IN_CORSO: il tecnico sta lavorando sull'intervento
 * - COMPLETATO: intervento concluso
 * - ANNULLATO: intervento cancellato (es. falso allarme)
 */
public enum StatoIntervento {
    DA_FARE,
    IN_CORSO,
    COMPLETATO,
    ANNULLATO
}