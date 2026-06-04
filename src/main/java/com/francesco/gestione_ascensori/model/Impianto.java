package com.francesco.gestione_ascensori.model;

import jakarta.persistence.*;

@Entity
@Table(name = "impianti")
public class Impianto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    // Matricola ufficiale dell'impianto
    @Column(length = 100)
    private String matricola;

    @Column(nullable = false, length = 255)
    private String indirizzo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "luogo_id")
    private Luogo luogo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatoImpianto stato;

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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getMatricola() { return matricola; }
    public void setMatricola(String matricola) { this.matricola = matricola; }

    public String getIndirizzo() { return indirizzo; }
    public void setIndirizzo(String indirizzo) { this.indirizzo = indirizzo; }

    public Luogo getLuogo() { return luogo; }
    public void setLuogo(Luogo luogo) { this.luogo = luogo; }

    public StatoImpianto getStato() { return stato; }
    public void setStato(StatoImpianto stato) { this.stato = stato; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
