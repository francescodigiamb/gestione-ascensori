package com.francesco.gestione_ascensori.model;

/**
 * ✅ Stato dell'impianto (ascensore).
 * - ATTIVO: impianto regolarmente in uso
 * - SOSPESO: impianto temporaneamente fuori servizio (es. in attesa pezzi)
 * - DISMESSO: impianto rimosso / non più in gestione
 */
public enum StatoImpianto {
    ATTIVO,
    SOSPESO,
    DISMESSO
}