package com.francesco.gestione_ascensori.model;

import jakarta.persistence.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "giornate_lavorative",
       uniqueConstraints = @UniqueConstraint(columnNames = {"utente_id", "data"}))
public class GiornataLavorativa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "utente_id")
    private Utente utente;

    @Column(nullable = false)
    private LocalDate data;

    // Ora di inizio giornata (timbra entrata)
    private LocalTime oraInizio;

    // Ora di fine giornata (timbra uscita) — null finché non termina
    private LocalTime oraFine;

    // true = bloccata, l'operatore non può più modificarla (solo admin)
    @Column(nullable = false)
    private boolean bloccata = false;

    public GiornataLavorativa() {}

    public GiornataLavorativa(Utente utente, LocalDate data, LocalTime oraInizio) {
        this.utente = utente;
        this.data = data;
        this.oraInizio = oraInizio;
    }

    // Calcola durata in minuti (0 se incompleta)
    public long getMinutiLavorati() {
        if (oraInizio == null || oraFine == null) return 0;
        return Duration.between(oraInizio, oraFine).toMinutes();
    }

    // Es. "7h 30m"
    public String getDurataFormattata() {
        long minuti = getMinutiLavorati();
        if (minuti <= 0) return "—";
        return String.format("%dh %02dm", minuti / 60, minuti % 60);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Utente getUtente() { return utente; }
    public void setUtente(Utente utente) { this.utente = utente; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public LocalTime getOraInizio() { return oraInizio; }
    public void setOraInizio(LocalTime oraInizio) { this.oraInizio = oraInizio; }

    public LocalTime getOraFine() { return oraFine; }
    public void setOraFine(LocalTime oraFine) { this.oraFine = oraFine; }

    public boolean isBloccata() { return bloccata; }
    public void setBloccata(boolean bloccata) { this.bloccata = bloccata; }
}
