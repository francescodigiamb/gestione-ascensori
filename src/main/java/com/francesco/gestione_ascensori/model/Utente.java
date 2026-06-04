package com.francesco.gestione_ascensori.model;

import jakarta.persistence.*;

@Entity
@Table(name = "utenti")
public class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    // Nome e cognome da mostrare nell'app
    @Column(nullable = false, length = 100)
    private String nomeCompleto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RuoloUtente ruolo;

    public Utente() {}

    public Utente(String username, String password, String nomeCompleto, RuoloUtente ruolo) {
        this.username = username;
        this.password = password;
        this.nomeCompleto = nomeCompleto;
        this.ruolo = ruolo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }

    public RuoloUtente getRuolo() { return ruolo; }
    public void setRuolo(RuoloUtente ruolo) { this.ruolo = ruolo; }
}
