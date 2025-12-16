# Gestione Ascensori (Work in Progress)

Applicazione web per la gestione di **luoghi**, **impianti** e **interventi di manutenzione**.
Progetto personale sviluppato per consolidare Java + Spring Boot + MySQL.

## Stack
- Java, Spring Boot
- Spring MVC + Thymeleaf
- Spring Data JPA / Hibernate
- MySQL
- Maven

## Funzionalità principali
- CRUD Luoghi
- CRUD Impianti (associati a un luogo)
- CRUD Interventi / Manutenzioni (associati a un impianto)
- Navigazione tra liste/dettagli con pagine Thymeleaf

## Avvio in locale
1. Crea un database MySQL (es. `gestione_ascensori`)
2. Configura `application.properties` (URL, user, password)
3. Avvia:
   - `mvn spring-boot:run`
4. Apri: `http://localhost:8080`

## Note
Progetto in sviluppo: migliorie su UI, validazioni e flussi di modifica.
