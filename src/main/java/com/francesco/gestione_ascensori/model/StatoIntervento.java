package com.francesco.gestione_ascensori.model;

public enum StatoIntervento {
    DA_FARE("Da fare"),
    IN_CORSO("In corso"),
    COMPLETATO("Completato"),
    ANNULLATO("Annullato");

    private final String label;

    StatoIntervento(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
