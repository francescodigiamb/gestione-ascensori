package com.francesco.gestione_ascensori.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "interventi")
public class Intervento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "impianto_id")
    private Impianto impianto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoIntervento tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatoIntervento stato;

    // Data pianificata (solo giorno, senza orario)
    private LocalDate dataProgrammata;

    // Data effettiva di esecuzione (solo giorno, senza orario)
    private LocalDate dataEsecuzione;

    // Orario di inizio intervento (compilato dall'operatore)
    private LocalTime inizioIntervento;

    // Orario di fine intervento (compilato dall'operatore)
    private LocalTime fineIntervento;

    // Descrizione del lavoro da svolgere (gestita dall'admin)
    @Column(nullable = false, length = 500)
    private String descrizione;

    // Rapportino: cosa ha fatto il tecnico a fine lavoro (compilato dall'operatore)
    @Column(length = 2000)
    private String noteTecnico;

    @Column(precision = 10, scale = 2)
    private BigDecimal costo;

    public Intervento() {
    }

    public Intervento(Impianto impianto, TipoIntervento tipo, StatoIntervento stato,
                      LocalDate dataProgrammata, String descrizione) {
        this.impianto = impianto;
        this.tipo = tipo;
        this.stato = stato;
        this.dataProgrammata = dataProgrammata;
        this.descrizione = descrizione;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Impianto getImpianto() { return impianto; }
    public void setImpianto(Impianto impianto) { this.impianto = impianto; }

    public TipoIntervento getTipo() { return tipo; }
    public void setTipo(TipoIntervento tipo) { this.tipo = tipo; }

    public StatoIntervento getStato() { return stato; }
    public void setStato(StatoIntervento stato) { this.stato = stato; }

    public LocalDate getDataProgrammata() { return dataProgrammata; }
    public void setDataProgrammata(LocalDate dataProgrammata) { this.dataProgrammata = dataProgrammata; }

    public LocalDate getDataEsecuzione() { return dataEsecuzione; }
    public void setDataEsecuzione(LocalDate dataEsecuzione) { this.dataEsecuzione = dataEsecuzione; }

    public LocalTime getInizioIntervento() { return inizioIntervento; }
    public void setInizioIntervento(LocalTime inizioIntervento) { this.inizioIntervento = inizioIntervento; }

    public LocalTime getFineIntervento() { return fineIntervento; }
    public void setFineIntervento(LocalTime fineIntervento) { this.fineIntervento = fineIntervento; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public String getNoteTecnico() { return noteTecnico; }
    public void setNoteTecnico(String noteTecnico) { this.noteTecnico = noteTecnico; }

    public BigDecimal getCosto() { return costo; }
    public void setCosto(BigDecimal costo) { this.costo = costo; }
}
