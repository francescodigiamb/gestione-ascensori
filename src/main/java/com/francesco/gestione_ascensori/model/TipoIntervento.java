package com.francesco.gestione_ascensori.model;

/**
 * ✅ Tipo di intervento:
 * - MANUTENZIONE: manutenzione programmata (tagliando)
 * - GUASTO: intervento per guasto / emergenza
 * - VERIFICA: controlli, ispezioni, sopralluoghi
 * - ALTRO: qualunque altra attività non classificata
 */
public enum TipoIntervento {
    MANUTENZIONE,
    GUASTO,
    VERIFICA,
    ALTRO
}