package com.francesco.gestione_ascensori.model;

import jakarta.persistence.*;

/**
 * ✅ Luogo
 * Rappresenta un'area/città/zona in cui si trovano gli impianti.
 * Esempi: "Pescara", "Chieti", "Montesilvano".
 */
@Entity
@Table(name = "luoghi")
public class Luogo {

    // Chiave primaria autoincrement
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nome del luogo (es. "Pescara")
    @Column(nullable = false, unique = true, length = 100)
    private String nome;

    // Eventuale descrizione / note sul luogo
    @Column(length = 255)
    private String descrizione;

    // ➕ Costruttore vuoto richiesto da JPA
    public Luogo() {
    }

    // Costruttore comodo per creare rapidamente oggetti in Java
    public Luogo(String nome, String descrizione) {
        this.nome = nome;
        this.descrizione = descrizione;
    }

    // Getter e Setter (li lascio espliciti per chiarezza)

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

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
}
