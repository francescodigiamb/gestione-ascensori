package com.francesco.gestione_ascensori.model;

public enum TipoIntervento {
    MANUTENZIONE("Manutenzione"),
    GUASTO("Guasto"),
    VERIFICA("Verifica"),
    MONTAGGIO("Montaggio"),
    ALTRO("Altro");

    private final String label;

    TipoIntervento(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
