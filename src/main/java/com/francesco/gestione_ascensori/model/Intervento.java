package com.francesco.gestione_ascensori.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ✅ Intervento
 * Rappresenta un lavoro (manutenzione, guasto, verifica, ecc.) su un impianto.
 * Ogni intervento ha un costo e uno stato (DA_FARE, COMPLETATO, ecc.).
 */
@Entity
@Table(name = "interventi")
public class Intervento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Impianto su cui viene effettuato l'intervento
    @ManyToOne(optional = false)
    @JoinColumn(name = "impianto_id")
    private Impianto impianto;

    // Tipo di intervento (MANUTENZIONE, GUASTO, VERIFICA, ALTRO)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoIntervento tipo;

    // Stato dell'intervento (DA_FARE, IN_CORSO, COMPLETATO, ANNULLATO)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatoIntervento stato;

    // Data/ora programmata (quando è previsto l'intervento)
    private LocalDateTime dataProgrammata;

    // Data/ora effettiva di esecuzione (quando viene completato)
    private LocalDateTime dataEsecuzione;

    // Descrizione della richiesta / attività da svolgere
    @Column(nullable = false, length = 500)
    private String descrizione;

    // Note del tecnico a fine lavoro
    @Column(length = 1000)
    private String noteTecnico;

    // Costo dell'intervento (definito dall'admin)
    @Column(precision = 10, scale = 2)
    private BigDecimal costo;

    public Intervento() {
    }

    public Intervento(Impianto impianto,
            TipoIntervento tipo,
            StatoIntervento stato,
            LocalDateTime dataProgrammata,
            String descrizione) {
        this.impianto = impianto;
        this.tipo = tipo;
        this.stato = stato;
        this.dataProgrammata = dataProgrammata;
        this.descrizione = descrizione;
    }

    // Getter e Setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Impianto getImpianto() {
        return impianto;
    }

    public void setImpianto(Impianto impianto) {
        this.impianto = impianto;
    }

    public TipoIntervento getTipo() {
        return tipo;
    }

    public void setTipo(TipoIntervento tipo) {
        this.tipo = tipo;
    }

    public StatoIntervento getStato() {
        return stato;
    }

    public void setStato(StatoIntervento stato) {
        this.stato = stato;
    }

    public LocalDateTime getDataProgrammata() {
        return dataProgrammata;
    }

    public void setDataProgrammata(LocalDateTime dataProgrammata) {
        this.dataProgrammata = dataProgrammata;
    }

    public LocalDateTime getDataEsecuzione() {
        return dataEsecuzione;
    }

    public void setDataEsecuzione(LocalDateTime dataEsecuzione) {
        this.dataEsecuzione = dataEsecuzione;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getNoteTecnico() {
        return noteTecnico;
    }

    public void setNoteTecnico(String noteTecnico) {
        this.noteTecnico = noteTecnico;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }
}
