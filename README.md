# Gestione Impianti Ascensori (Work in Progress)

Applicazione web per la gestione di **luoghi**, **impianti** e **interventi di manutenzione**.
Progetto personale sviluppato per consolidare competenze in **Java**, **Spring Boot** e **MySQL**.

## Stack Tecnologico
- Java
- Spring Boot
- Spring MVC + Thymeleaf
- Spring Data JPA / Hibernate
- MySQL
- Maven

## Funzionalità principali
- Gestione CRUD dei luoghi
- Gestione CRUD degli impianti associati a un luogo
- Gestione CRUD degli interventi di manutenzione
- Relazioni tra entità (Luogo → Impianto → Intervento)
- Navigazione tra liste e dettagli con Thymeleaf
  
## Avvio in locale
1. Crea un database MySQL (es. `gestione_ascensori`)
2. Configura il file `application.properties`
3. Avvia l’applicazione:
   - `mvn spring-boot:run`
4. Apri il browser su `http://localhost:8080`

## Stato del progetto
Il progetto è attualmente in sviluppo.  
Sono previste migliorie su interfaccia utente, validazioni dei dati e flussi di modifica.
