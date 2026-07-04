# Progetto WebDelivery - Fooody

Questo repository contiene l'intera implementazione del progetto "WebDelivery" per i corsi di **Web Engineering** e **Sviluppo Web Avanzato**. Il sistema è progettato come un'architettura disaccoppiata composta da due moduli principali: un backend API RESTful e una Web App di Frontend.

## Struttura del Progetto

Il progetto è organizzato in un'architettura Maven:

* **/api**: Contiene il backend RESTful sviluppato in Jakarta EE. Gestisce la persistenza dei dati, la sicurezza tramite JWT e la logica di business.
* **/webapp**: Contiene la Web App di Frontend (Servlet + FreeMarker). Funge da client per l'API REST, gestisce la User Interface e la logica di presentazione.
* **/documentazione e database**: Contiene la documentazione tecnica in formato Markdown e lo script SQL per il database.

## Documentazione

Per una consultazione dettagliata delle specifiche tecniche, dell'architettura e delle scelte progettuali, si rimanda ai seguenti documenti:

* [Documentazione Web Engineering (SWA)](https://github.com/03Maikol33/Fooody/blob/main/documentazione%20e%20database/Documentazione-SWA-Gasparroni.md): Analisi dettagliata dei requisiti, diagrammi ER e relazionale, e scelte architetturali.
* [Documentazione Web Engineering (WE)](https://github.com/03Maikol33/Fooody/blob/main/documentazione%20e%20database/Documentazione-WE-Gasparroni.md): Analisi delle funzionalità, layout del sito e test di validazione.
* [Specifiche API (OpenAPI)](https://github.com/03Maikol33/Fooody/blob/main/documentazione%20e%20database/openapi.md): Documentazione tecnica completa degli endpoint REST, payload e codici di stato HTTP.

## Prerequisiti

Per eseguire il progetto sono necessari:

* Java JDK 17 o superiore.
* Apache Tomcat 10+ (o container Jakarta EE compatibile).
* MySQL Server 8.0+.
* Maven 3.6+.

## Istruzioni per l'avvio

1. **Database**: Creare un database MySQL denominato `WebDelivery` ed eseguire lo script `documentazione e database/dump.sql` per popolare le tabelle.
2. **Configurazione**: Verificare i file di configurazione (`DatabaseManager.java` nell'API) per assicurarsi che le credenziali del database (user/password) corrispondano alla propria installazione locale.
3. **Compilazione**:
* Navigare nella cartella `/api` ed eseguire `mvn clean install`.
* Navigare nella cartella `/webapp` ed eseguire `mvn clean install`.


4. **Deploy**: Effettuare il deploy dei file `.war` generati su Apache Tomcat.

## Credenziali di Accesso nella Web App

Per testare rapidamente tutti i ruoli dell'applicazione, è possibile utilizzare le seguenti credenziali predefinite:
### Cliente di esempio
* **Email**: `maikol@cliente.it`
* **Password**: `hashedpassword3`

### Staff di esempio
* **Email**: `staff@webdelivery.it`
* **Password**: `hashedpassword2`

### Proprietario di esempio
* **Email**: `admin@webdelivery.it`
* **Password**: `hashedpassword1`
