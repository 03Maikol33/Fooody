# Fooody REST API

_Version: 1.0.0_

API RESTful per la gestione delle ordinazioni e delle consegne a domicilio dell'attività Fooody.

## Servers
- **http://localhost:8080/Fooody/api** — Server locale (Apache Tomcat)

## Table of Contents

- [`POST` /auth/login](#post-authlogin)
- [`POST` /auth/register](#post-authregister)
- [`GET` /prodotti](#get-prodotti)
- [`POST` /prodotti](#post-prodotti)
- [`PUT` /prodotti/{id}](#put-prodottiid)
- [`POST` /prodotti/{idProdotto}/caratteristiche](#post-prodottiidprodottocaratteristiche)
- [`DELETE` /prodotti/{idProdotto}/caratteristiche/{idCaratteristica}](#delete-prodottiidprodottocaratteristicheidcaratteristica)
- [`GET` /prodotti/{idProdotto}/ingredienti](#get-prodottiidprodottoingredienti)
- [`GET` /prodotti/categorie](#get-prodotticategorie)
- [`GET` /ingredienti](#get-ingredienti)
- [`POST` /ingredienti](#post-ingredienti)
- [`DELETE` /ingredienti/{id}](#delete-ingredientiid)
- [`GET` /caratteristiche](#get-caratteristiche)
- [`POST` /caratteristiche](#post-caratteristiche)
- [`PUT` /caratteristiche/{idCaratteristica}](#put-caratteristicheidcaratteristica)
- [`DELETE` /caratteristiche/{idCaratteristica}](#delete-caratteristicheidcaratteristica)
- [`GET` /caratteristiche/gruppi](#get-caratteristichegruppi)
- [`POST` /caratteristiche/gruppi](#post-caratteristichegruppi)
- [`PUT` /caratteristiche/gruppi/{idGmc}](#put-caratteristichegruppiidgmc)
- [`DELETE` /caratteristiche/gruppi/{idGmc}](#delete-caratteristichegruppiidgmc)
- [`GET` /ordini](#get-ordini)
- [`POST` /ordini](#post-ordini)
- [`DELETE` /ordini/{idOrdine}](#delete-ordiniidordine)
- [`POST` /ordini/{idOrdine}/dettagli](#post-ordiniidordinedettagli)
- [`GET` /ordini/{idOrdine}/dettagli](#get-ordiniidordinedettagli)
- [`GET` /ordini/{idOrdine}/tempo-stimato](#get-ordiniidordinetempo-stimato)
- [`GET` /ordini/{idOrdine}/prezzo-totale](#get-ordiniidordineprezzo-totale)
- [`POST` /ordini/{idOrdine}/stati](#post-ordiniidordinestati)
- [`GET` /ordini/{idOrdine}/operatori](#get-ordiniidordineoperatori)
- [`GET` /personale](#get-personale)
- [`DELETE` /personale/{id}](#delete-personaleid)
- [`POST` /personale/{idUtente}](#post-personaleidutente)
- [`PUT` /clienti/{idUtente}](#put-clientiidutente)
- [`GET` /clienti/{idUtente}/ordini](#get-clientiidutenteordini)


## Endpoints

### `/auth/login`

#### POST

**Operation ID:** `POST-/auth/login`
**Summary:** Effettua il login

Permette agli utenti di autenticarsi e ottenere un token JWT per accedere alle risorse protette.

**Request Body:**

| Media type | Schema |
| ---------- | ------ |
| `application/json` | `#/components/schemas/LoginRequest` |

**Responses:**

| Code | Description |
| ---- | ----------- |
| `200` | Login effettuato con successo. |
| `401` | Credenziali non valide o utente non trovato. |

**Response Examples — `200`**

### `/auth/register`

#### POST

**Operation ID:** `POST-/auth/register`
**Summary:** Registra un nuovo utente

Crea un nuovo account cliente nel sistema.

**Request Body:**

| Media type | Schema |
| ---------- | ------ |
| `application/json` | `#/components/schemas/Utente` |

**Responses:**

| Code | Description |
| ---- | ----------- |
| `201` | Registrazione completata con successo. |
| `400` | Dati obbligatori mancanti. |
| `409` | Impossibile creare l'utente. L'email potrebbe essere già in uso. |

### `/prodotti`

#### GET

**Operation ID:** `GET-/prodotti`
**Summary:** Elenco di tutti i prodotti e ricerca con filtri.

Restituisce una lista di tutti i prodotti disponibili nel sistema. Supporta la ricerca con filtri per nome, fascia di prezzo e categoria.

**Parameters:**

| Name | In | Required | Type | Description |
| ---- | -- | -------- | ---- | ----------- |
| `categoria` | query | no | string | Filtro per categoria del prodotto (es. "Pizza", "Bevande"). |
| `nome` | query | no | string | Filtro per nome del prodotto. |
| `prezzoMin` | query | no | number | Filtro per prezzo minimo del prodotto. |
| `prezzoMax` | query | no | number | Filtro per prezzo massimo del prodotto. |

**Responses:**

| Code | Description |
| ---- | ----------- |
| `200` | Elenco dei prodotti restituito con successo. |

**Response Examples — `200`**

#### POST

**Operation ID:** `POST-/prodotti`
**Summary:** Aggiunge un nuovo prodotto al menu

**Request Body:**

| Media type | Schema |
| ---------- | ------ |
| `application/json` | `#/components/schemas/Prodotto` |

**Responses:**

| Code | Description |
| ---- | ----------- |
| `201` | Prodotto aggiunto con successo. |
| `403` | Accesso negato. Solo il proprietario può effettuare questa operazione. |
| `500` | Errore interno del server. |

### `/prodotti/{id}`

#### PUT

**Operation ID:** `PUT-/prodotti/{id}`
**Summary:** Aggiorna un prodotto esistente

**Parameters:**

| Name | In | Required | Type | Description |
| ---- | -- | -------- | ---- | ----------- |
| `id` | path | yes | integer |  |

**Request Body:**

| Media type | Schema |
| ---------- | ------ |
| `application/json` | `#/components/schemas/Prodotto` |

**Responses:**

| Code | Description |
| ---- | ----------- |
| `200` | Prodotto aggiornato con successo. |
| `403` | Accesso negato. |
| `500` | Errore interno del server. |

### `/prodotti/{idProdotto}/caratteristiche`

#### POST

**Operation ID:** `POST-/prodotti/{idProdotto}/caratteristiche`
**Summary:** Assegna una caratteristica esistente a un prodotto.

**Parameters:**

| Name | In | Required | Type | Description |
| ---- | -- | -------- | ---- | ----------- |
| `idProdotto` | path | yes | integer |  |

**Request Body:**

| Media type | Schema |
| ---------- | ------ |
| `application/json` | `#/components/schemas/Caratteristica` |

**Responses:**

| Code | Description |
| ---- | ----------- |
| `201` | Caratteristica assegnata con successo. |
| `400` | ID caratteristica mancante. |
| `403` | Accesso negato. |
| `500` | Errore durante l'assegnazione. |

### `/prodotti/{idProdotto}/caratteristiche/{idCaratteristica}`

#### DELETE

**Operation ID:** `DELETE-/prodotti/{idProdotto}/caratteristiche/{idCaratteristica}`
**Summary:** Rimuove una caratteristica da un prodotto.

Rimuove una caratteristica specifica da un prodotto esistente.

**Parameters:**

| Name | In | Required | Type | Description |
| ---- | -- | -------- | ---- | ----------- |
| `idProdotto` | path | yes | integer | ID del prodotto da cui rimuovere la caratteristica. |
| `idCaratteristica` | path | yes | integer | ID della caratteristica da rimuovere dal prodotto. |

**Responses:**

| Code | Description |
| ---- | ----------- |
| `204` | Caratteristica rimossa con successo. |
| `404` | Prodotto o caratteristica non trovata. |

### `/prodotti/{idProdotto}/ingredienti`

#### GET

**Operation ID:** `GET-/prodotti/{idProdotto}/ingredienti`
**Summary:** Elenco ingredienti di un prodotto.

Restituisce la lista degli ingredienti associati a un prodotto specifico. Se si utilizza un token di Proprietario o di Personale, vengono restituite anche le quantità per ciascun ingrediente.

**Parameters:**

| Name | In | Required | Type | Description |
| ---- | -- | -------- | ---- | ----------- |
| `idProdotto` | path | yes | integer | ID del prodotto di cui ottenere gli ingredienti. |

**Responses:**

| Code | Description |
| ---- | ----------- |
| `200` | Elenco degli ingredienti restituito con successo. |

**Response Examples — `200`**

### `/prodotti/categorie`

#### GET

**Operation ID:** `GET-/prodotti/categorie`
**Summary:** Ottiene l'elenco delle categorie uniche

Restituisce una lista di tutte le categorie di prodotti presenti nel menu.

**Responses:**

| Code | Description |
| ---- | ----------- |
| `200` | Elenco delle categorie restituito con successo. |

**Response Examples — `200`**

### `/ingredienti`

#### GET

**Operation ID:** `GET-/ingredienti`
**Summary:** Elenco di tutti gli ingredienti

Restituisce l'elenco globale degli ingredienti disponibili nel sistema.

**Responses:**

| Code | Description |
| ---- | ----------- |
| `200` | Elenco degli ingredienti |

**Response Examples — `200`**

#### POST

**Operation ID:** `POST-/ingredienti`
**Summary:** Crea un nuovo ingrediente

Aggiunge un nuovo ingrediente al dizionario globale.

**Request Body:**

| Media type | Schema |
| ---------- | ------ |
| `application/json` | `#/components/schemas/Ingrediente` |

**Responses:**

| Code | Description |
| ---- | ----------- |
| `201` | Ingrediente creato con successo. |
| `403` | Accesso negato. Solo il proprietario può effettuare questa operazione. |
| `500` | Errore interno del server. |

### `/ingredienti/{id}`

#### DELETE

**Operation ID:** `DELETE-/ingredienti/{id}`
**Summary:** Elimina un ingrediente

Rimuove un ingrediente dal dizionario globale.

**Parameters:**

| Name | In | Required | Type | Description |
| ---- | -- | -------- | ---- | ----------- |
| `id` | path | yes | integer |  |

**Responses:**

| Code | Description |
| ---- | ----------- |
| `204` | Ingrediente eliminato con successo. |
| `403` | Accesso negato. |
| `409` | Conflitto - Ingrediente in uso in alcune ricette. |

### `/caratteristiche`

#### GET

**Operation ID:** `GET-/caratteristiche`
**Summary:** Elenco di tutte le caratteristiche

**Responses:**

| Code | Description |
| ---- | ----------- |
| `200` | Lista delle caratteristiche |

**Response Examples — `200`**

#### POST

**Operation ID:** `POST-/caratteristiche`
**Summary:** Crea una nuova caratteristica (opzione menu)

**Request Body:**

| Media type | Schema |
| ---------- | ------ |
| `application/json` | `#/components/schemas/Caratteristica` |

**Responses:**

| Code | Description |
| ---- | ----------- |
| `201` | Caratteristica creata con successo. |
| `403` | Accesso negato. |
| `500` | Errore interno del server. |

### `/caratteristiche/{idCaratteristica}`

#### PUT

**Operation ID:** `PUT-/caratteristiche/{idCaratteristica}`
**Summary:** Aggiorna una caratteristica esistente

**Parameters:**

| Name | In | Required | Type | Description |
| ---- | -- | -------- | ---- | ----------- |
| `idCaratteristica` | path | yes | integer |  |

**Request Body:**

| Media type | Schema |
| ---------- | ------ |
| `application/json` | `#/components/schemas/Caratteristica` |

**Responses:**

| Code | Description |
| ---- | ----------- |
| `200` | Caratteristica aggiornata con successo. |
| `403` | Accesso negato. |

#### DELETE

**Operation ID:** `DELETE-/caratteristiche/{idCaratteristica}`
**Summary:** Elimina una caratteristica

**Parameters:**

| Name | In | Required | Type | Description |
| ---- | -- | -------- | ---- | ----------- |
| `idCaratteristica` | path | yes | integer |  |

**Responses:**

| Code | Description |
| ---- | ----------- |
| `204` | Caratteristica eliminata con successo. |
| `403` | Accesso negato. |
| `409` | Conflitto - Caratteristica in uso. |

### `/caratteristiche/gruppi`

#### GET

**Operation ID:** `GET-/caratteristiche/gruppi`
**Summary:** Elenco di tutti i gruppi di mutua esclusione

**Responses:**

| Code | Description |
| ---- | ----------- |
| `200` | Lista dei gruppi |

**Response Examples — `200`**

#### POST

**Operation ID:** `POST-/caratteristiche/gruppi`
**Summary:** Crea un nuovo gruppo di mutua esclusione

**Request Body:**

| Media type | Schema |
| ---------- | ------ |
| `application/json` | `#/components/schemas/GruppoMutuaEsclusione` |

**Responses:**

| Code | Description |
| ---- | ----------- |
| `201` | Gruppo creato con successo. |
| `403` | Accesso negato. |
| `500` | Errore interno del server. |

### `/caratteristiche/gruppi/{idGmc}`

#### PUT

**Operation ID:** `PUT-/caratteristiche/gruppi/{idGmc}`
**Summary:** Aggiorna un gruppo esistente

**Parameters:**

| Name | In | Required | Type | Description |
| ---- | -- | -------- | ---- | ----------- |
| `idGmc` | path | yes | integer |  |

**Request Body:**

| Media type | Schema |
| ---------- | ------ |
| `application/json` | `#/components/schemas/GruppoMutuaEsclusione` |

**Responses:**

| Code | Description |
| ---- | ----------- |
| `200` | Gruppo aggiornato con successo. |
| `403` | Accesso negato. |

#### DELETE

**Operation ID:** `DELETE-/caratteristiche/gruppi/{idGmc}`
**Summary:** Elimina un gruppo di mutua esclusione

**Parameters:**

| Name | In | Required | Type | Description |
| ---- | -- | -------- | ---- | ----------- |
| `idGmc` | path | yes | integer |  |

**Responses:**

| Code | Description |
| ---- | ----------- |
| `204` | Gruppo eliminato con successo. |
| `403` | Accesso negato. |
| `409` | Conflitto - Gruppo in uso da caratteristiche. |

### `/ordini`

#### GET

**Operation ID:** `GET-/ordini`
**Summary:** Elenco ordini con filtri.

**Parameters:**

| Name | In | Required | Type | Description |
| ---- | -- | -------- | ---- | ----------- |
| `dataInserimento` | query | no | string |  |
| `stato` | query | no | string |  |

**Responses:**

| Code | Description |
| ---- | ----------- |
| `200` | Elenco degli ordini restituito con successo. |

**Response Examples — `200`**

#### POST

**Operation ID:** `POST-/ordini`
**Summary:** Creazione di un nuovo ordine completo

Inserisce l'ordine intero comprensivo di prodotti e caratteristiche, ricalcolando automaticamente totale e tempistiche.

**Request Body:**

| Media type | Schema |
| ---------- | ------ |
| `application/json` | `#/components/schemas/NuovoOrdineRequest` |

**Responses:**

| Code | Description |
| ---- | ----------- |
| `201` | Ordine creato con successo. |
| `400` | Impossibile creare un ordine vuoto. |
| `500` | Errore interno del server durante la creazione dell'ordine. |

### `/ordini/{idOrdine}`

#### DELETE

**Operation ID:** `DELETE-/ordini/{idOrdine}`
**Summary:** Annulla ordine

**Parameters:**

| Name | In | Required | Type | Description |
| ---- | -- | -------- | ---- | ----------- |
| `idOrdine` | path | yes | integer | ID dell'ordine da annullare. |

**Responses:**

| Code | Description |
| ---- | ----------- |
| `204` | Ordine annullato con successo. |
| `404` | Ordine non trovato. |

### `/ordini/{idOrdine}/dettagli`

#### POST

**Operation ID:** `POST-/ordini/{idOrdine}/dettagli`
**Summary:** Insermento prodotto in ordine

**Parameters:**

| Name | In | Required | Type | Description |
| ---- | -- | -------- | ---- | ----------- |
| `idOrdine` | path | yes | integer | ID dell'ordine a cui aggiungere il prodotto. |

**Request Body:**

| Media type | Schema |
| ---------- | ------ |
| `application/json` | `#/components/schemas/NuovoDettaglioRequest` |

**Responses:**

| Code | Description |
| ---- | ----------- |
| `201` | Prodotto aggiunto all'ordine con successo. |

#### GET

**Operation ID:** `GET-/ordini/{idOrdine}/dettagli`
**Summary:** Elenco prodotti in un ordine

**Parameters:**

| Name | In | Required | Type | Description |
| ---- | -- | -------- | ---- | ----------- |
| `idOrdine` | path | yes | integer | ID dell'ordine di cui ottenere i dettagli. |

**Responses:**

| Code | Description |
| ---- | ----------- |
| `200` | Elenco dei prodotti nell'ordine restituito con successo. |

**Response Examples — `200`**

### `/ordini/{idOrdine}/tempo-stimato`

#### GET

**Operation ID:** `GET-/ordini/{idOrdine}/tempo-stimato`
**Summary:** Tempo stimato per la consegna.

**Parameters:**

| Name | In | Required | Type | Description |
| ---- | -- | -------- | ---- | ----------- |
| `idOrdine` | path | yes | integer | ID dell'ordine di cui ottenere il tempo stimato per la consegna. |

**Responses:**

| Code | Description |
| ---- | ----------- |
| `200` | Tempo stimato per la consegna restituito con successo. |

**Response Examples — `200`**

### `/ordini/{idOrdine}/prezzo-totale`

#### GET

**Operation ID:** `GET-/ordini/{idOrdine}/prezzo-totale`
**Summary:** Prezzo totale dell'ordine.

**Parameters:**

| Name | In | Required | Type | Description |
| ---- | -- | -------- | ---- | ----------- |
| `idOrdine` | path | yes | integer | ID dell'ordine di cui ottenere il prezzo totale. |

**Responses:**

| Code | Description |
| ---- | ----------- |
| `200` | Prezzo totale dell'ordine restituito con successo. |

**Response Examples — `200`**

### `/ordini/{idOrdine}/stati`

#### POST

**Operation ID:** `POST-/ordini/{idOrdine}/stati`
**Summary:** Aggiorna lo stato di un ordine

**Parameters:**

| Name | In | Required | Type | Description |
| ---- | -- | -------- | ---- | ----------- |
| `idOrdine` | path | yes | integer | ID dell'ordine di cui aggiornare lo stato. |

**Request Body:**

| Media type | Schema |
| ---------- | ------ |
| `application/json` | `object` |

**Responses:**

| Code | Description |
| ---- | ----------- |
| `200` | Stato dell'ordine aggiornato con successo. |

### `/ordini/{idOrdine}/operatori`

#### GET

**Operation ID:** `GET-/ordini/{idOrdine}/operatori`
**Summary:** Elenco operatori coinvolti in un ordine

**Parameters:**

| Name | In | Required | Type | Description |
| ---- | -- | -------- | ---- | ----------- |
| `idOrdine` | path | yes | integer | ID dell'ordine di cui ottenere l'elenco degli operatori coinvolti. |

**Responses:**

| Code | Description |
| ---- | ----------- |
| `200` | Elenco degli operatori coinvolti nell'ordine restituito con successo. |

**Response Examples — `200`**

### `/personale`

#### GET

**Operation ID:** `GET-/personale`
**Summary:** Ottiene l'elenco di tutto il personale

**Responses:**

| Code | Description |
| ---- | ----------- |
| `200` | Elenco del personale |

**Response Examples — `200`**

### `/personale/{id}`

#### DELETE

**Operation ID:** `DELETE-/personale/{id}`
**Summary:** Licenzia (rimuove) un membro del personale

**Parameters:**

| Name | In | Required | Type | Description |
| ---- | -- | -------- | ---- | ----------- |
| `id` | path | yes | integer |  |

**Responses:**

| Code | Description |
| ---- | ----------- |
| `204` | Personale rimosso con successo. |
| `403` | Accesso negato. |
| `404` | Personale non trovato. |

### `/personale/{idUtente}`

#### POST

**Operation ID:** `POST-/personale/{idUtente}`
**Summary:** Promuove un utente esistente a membro del personale

**Parameters:**

| Name | In | Required | Type | Description |
| ---- | -- | -------- | ---- | ----------- |
| `idUtente` | path | yes | integer |  |

**Responses:**

| Code | Description |
| ---- | ----------- |
| `201` | Utente promosso con successo. |
| `403` | Accesso negato. |
| `500` | Errore interno. |

### `/clienti/{idUtente}`

#### PUT

**Operation ID:** `PUT-/clienti/{idUtente}`
**Summary:** Aggiorna il profilo di un cliente

**Parameters:**

| Name | In | Required | Type | Description |
| ---- | -- | -------- | ---- | ----------- |
| `idUtente` | path | yes | integer |  |

**Request Body:**

| Media type | Schema |
| ---------- | ------ |
| `application/json` | `#/components/schemas/Utente` |

**Responses:**

| Code | Description |
| ---- | ----------- |
| `200` | Profilo aggiornato con successo. |
| `403` | Non puoi modificare il profilo di altri utenti. |
| `500` | Errore durante l'aggiornamento. |

### `/clienti/{idUtente}/ordini`

#### GET

**Operation ID:** `GET-/clienti/{idUtente}/ordini`
**Summary:** Elenco degli ordini di un cliente

**Parameters:**

| Name | In | Required | Type | Description |
| ---- | -- | -------- | ---- | ----------- |
| `idUtente` | path | yes | integer | ID dell'utente di cui ottenere gli ordini. |

**Responses:**

| Code | Description |
| ---- | ----------- |
| `200` | Elenco degli ordini del cliente restituito con successo. |

**Response Examples — `200`**

## Security

| Scheme | Type | In | Name | Description |
| ------ | ---- | -- | ---- | ----------- |
| `bearerAuth` | http |  |  | Utilizza un token JWT per autenticarsi nelle richieste alle risorse protette. Il token deve essere incluso nell'header Authorization con il prefisso "Bearer". |

## Schemas

### `Utente`

| Property | Type | Required | Description |
| -------- | ---- | -------- | ----------- |
| `idUtente` | integer | no |  |
| `nome` | string | no |  |
| `cognome` | string | no |  |
| `email` | string | no |  |
| `password` | string | no |  |
| `ruolo` | string | no |  |
| `telefono` | string | no |  |
| `via` | string | no |  |
| `civico` | string | no |  |
| `citta` | string | no |  |

### `Personale`

| Property | Type | Required | Description |
| -------- | ---- | -------- | ----------- |
| `idPersonale` | integer | no |  |
| `idUtente` | integer | no |  |
| `nome` | string | no |  |
| `cognome` | string | no |  |

### `LoginRequest`

| Property | Type | Required | Description |
| -------- | ---- | -------- | ----------- |
| `email` | string | yes |  |
| `password` | string | yes |  |

### `LoginResponse`

| Property | Type | Required | Description |
| -------- | ---- | -------- | ----------- |
| `token` | string | no | Token JWT da utilizzare per autenticarsi nelle richieste successive. |
| `utente` | object | no |  |

### `GruppoMutuaEsclusione`

| Property | Type | Required | Description |
| -------- | ---- | -------- | ----------- |
| `idGmc` | integer | no |  |
| `nome` | string | no |  |
| `descrizione` | string | no |  |

### `Caratteristica`

| Property | Type | Required | Description |
| -------- | ---- | -------- | ----------- |
| `idCaratteristica` | integer | no |  |
| `nome` | string | no |  |
| `differenzaPrezzo` | number | no |  |
| `isDefault` | boolean | no |  |
| `idGmc` | integer | no |  |
| `gruppo` | string | no | Il nome del gruppo di caratteristiche in mutua esclusione se presente |

### `Prodotto`

| Property | Type | Required | Description |
| -------- | ---- | -------- | ----------- |
| `idProdotto` | integer | no |  |
| `categoria` | string | no |  |
| `nome` | string | no |  |
| `descrizione` | string | no |  |
| `prezzoBase` | number | no |  |
| `immagine` | string | no |  |
| `tempoPreparazione` | integer | no | Tempo di preparazione stimato in minuti |
| `procedura` | string | no | Procedura di preparazione del prodotto necessaria alla cucina. |
| `caratteristiche` | array<#/components/schemas/Caratteristica> | no |  |

### `Ingrediente`

| Property | Type | Required | Description |
| -------- | ---- | -------- | ----------- |
| `idIngrediente` | integer | no |  |
| `nome` | string | no |  |
| `quantita` | string | no |  |

### `Ordine`

| Property | Type | Required | Description |
| -------- | ---- | -------- | ----------- |
| `idOrdine` | integer | no |  |
| `timeInserimento` | string | no |  |
| `OrarioConsegnaRichiesto` | string | no |  |
| `statoCorrente` | string | no |  |
| `prezzoTotale` | number | no |  |

### `NuovoOrdineRequest`

| Property | Type | Required | Description |
| -------- | ---- | -------- | ----------- |
| `idCliente` | integer | no |  |
| `orarioConsegnaRichiesto` | string | no |  |
| `items` | array<#/components/schemas/NuovoDettaglioRequest> | no |  |

### `NuovoDettaglioRequest`

| Property | Type | Required | Description |
| -------- | ---- | -------- | ----------- |
| `idProdotto` | integer | no |  |
| `quantita` | integer | no |  |
| `caratteristicheScelte` | array<integer> | no |  |

### `DettaglioProdotto`

| Property | Type | Required | Description |
| -------- | ---- | -------- | ----------- |
| `idDettaglio` | integer | no |  |
| `nomeProdotto` | string | no |  |
| `quantita` | integer | no |  |
| `caratteristiche` | array<string> | no |  |
| `tempoPreparazione` | integer | no | Tempo di preparazione stimato in minuti per questo prodotto con le caratteristiche scelte |
| `procedura` | string | no | Procedura di preparazione del prodotto necessaria alla cucina, comprensiva delle caratteristiche scelte. |