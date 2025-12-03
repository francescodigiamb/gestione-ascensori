package com.francesco.gestione_ascensori.model;

import jakarta.persistence.*;

/**
 * ✅ Impianto
 * Rappresenta un singolo impianto (ascensore) gestito dall'azienda.
 * È collegato a un Luogo (es. Pescara) e avrà molti Interventi.
 */
@Entity
@Table(name = "impianti")
public class Impianto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nome o codice dell'impianto (es. "ASC-PE-001")
    @Column(nullable = false, length = 100)
    private String nome;

    // Indirizzo dettagliato dell'impianto
    @Column(nullable = false, length = 255)
    private String indirizzo;

    // Luogo/città di appartenenza (Pescara, Chieti, ecc.)
    @ManyToOne(optional = false)
    @JoinColumn(name = "luogo_id")
    private Luogo luogo;

    // Stato dell'impianto (ATTIVO, SOSPESO, DISMESSO)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatoImpianto stato;

    // Eventuali note (es. "Accesso dal cortile interno")
    @Column(length = 500)
    private String note;

    public Impianto() {
    }

    public Impianto(String nome, String indirizzo, Luogo luogo, StatoImpianto stato, String note) {
        this.nome = nome;
        this.indirizzo = indirizzo;
        this.luogo = luogo;
        this.stato = stato;
        this.note = note;
    }

    // Getter e Setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public Luogo getLuogo() {
        return luogo;
    }

    public void setLuogo(Luogo luogo) {
        this.luogo = luogo;
    }

    public StatoImpianto getStato() {
        return stato;
    }

    public void setStato(StatoImpianto stato) {
        this.stato = stato;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
