# Gestione Impianti Ascensori — Chiavaroli Ascensori

Applicazione web gestionale sviluppata su commissione per **Chiavaroli Ascensori**, una società di manutenzione di impianti elevatori.
Il sistema permette all'azienda di gestire impianti, interventi di manutenzione e operatori da un'unica interfaccia web, accessibile da qualsiasi dispositivo.

## Stack Tecnologico

| Tecnologia | Dettaglio |
|---|---|
| Java 21 | Linguaggio principale |
| Spring Boot 3.4 | Framework applicativo |
| Spring Security | Autenticazione e autorizzazione |
| Spring Data JPA / Hibernate | ORM e accesso al database |
| Thymeleaf | Template engine server-side |
| MySQL | Database relazionale |
| Bootstrap 5 + Bootstrap Icons | UI responsive |
| Maven | Build tool |
| Railway | Deploy e hosting cloud |

## Funzionalità

### Gestione impianti
- Organizzazione degli impianti per **luogo** (zona geografica)
- Scheda impianto con matricola, indirizzo, stato e note
- Storico completo degli interventi per ogni impianto
- Cambio di stato diretto dalla lista impianti

### Gestione interventi
- Creazione e assegnazione interventi agli operatori
- Tipi: manutenzione ordinaria, guasto, verifica
- Tracciamento stato: Da fare → In corso → Completato
- Rapportino tecnico obbligatorio per completare un intervento
- Registrazione di data programmata, data esecuzione e costo

### Multi-utente con ruoli
- **Admin**: accesso completo, gestione utenti e impianti
- **Operatore**: visualizza i propri interventi assegnati, aggiorna stato e compila rapportini

### Cartellino virtuale
- Gli operatori registrano entrata e uscita giornaliera
- L'admin visualizza il riepilogo mensile delle presenze per operatore

### Dashboard
- Interventi in scadenza entro 7 giorni evidenziati
- Alert in navbar con contatore interventi urgenti
- Riepilogo stato impianti

## Architettura

Applicazione **monolitica MVC** — scelta deliberata per mantenere semplicità e velocità di sviluppo per un contesto di utilizzo interno con pochi utenti. Nessuna API REST, nessun frontend separato.

```
Controller (Spring MVC)
    └── Service / Repository (Spring Data JPA)
            └── MySQL
```

## Deploy

L'applicazione è deployata su **Railway** con:
- Profilo Spring `prod` separato da quello di sviluppo
- Variabili d'ambiente per credenziali DB e configurazioni sensibili
- MySQL come servizio Railway collegato tramite variabili di riferimento

## Avvio in locale

1. Crea un database MySQL: `gestione_ascensori`
2. Configura `src/main/resources/application.properties` con le tue credenziali
3. Avvia:
   ```bash
   mvn spring-boot:run
   ```
4. Apri il browser su `http://localhost:8080`
5. Credenziali di default: `admin` / `Ascensori2024!`

## Stato del progetto

✅ Progetto completato e in produzione.
