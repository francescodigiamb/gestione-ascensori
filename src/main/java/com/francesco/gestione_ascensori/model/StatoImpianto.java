package com.francesco.gestione_ascensori.model;

public enum StatoImpianto {
    ATTIVO("Attivo"),
    SOSPESO("Sospeso"),
    DISMESSO("Dismesso");

    private final String label;

    StatoImpianto(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
