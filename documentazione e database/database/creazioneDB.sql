DROP DATABASE IF EXISTS WebDelivery;
CREATE DATABASE WebDelivery;
USE WebDelivery;

-- 1. UTENTE E RUOLI
CREATE TABLE Utente (
    idUtente INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nome VARCHAR(50) NOT NULL,
    cognome VARCHAR(50) NOT NULL,
    ruolo ENUM('proprietario', 'personale', 'cliente') NOT NULL
);

CREATE TABLE Proprietario (
    idProprietario INT AUTO_INCREMENT PRIMARY KEY,
    idUtente INT NOT NULL UNIQUE,
    FOREIGN KEY (idUtente) REFERENCES Utente(idUtente) ON DELETE CASCADE
);

CREATE TABLE Personale (
    idPersonale INT AUTO_INCREMENT PRIMARY KEY,
    idUtente INT NOT NULL UNIQUE,
    FOREIGN KEY (idUtente) REFERENCES Utente(idUtente) ON DELETE CASCADE
);

CREATE TABLE Cliente (
    idCliente INT AUTO_INCREMENT PRIMARY KEY,
    idUtente INT NOT NULL UNIQUE,
    telefono VARCHAR(20) NOT NULL,
    via VARCHAR(100) NOT NULL,
    civico VARCHAR(10) NOT NULL,
    citta VARCHAR(50) NOT NULL,
    FOREIGN KEY (idUtente) REFERENCES Utente(idUtente) ON DELETE CASCADE
);

-- 2. MENU E RICETTE
CREATE TABLE Prodotto (
    idProdotto INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE,
    categoria VARCHAR(50),
    descrizione TEXT NOT NULL,
    prezzoBase DECIMAL(6,2) NOT NULL,
    tempoPreparazione INT NOT NULL, -- Espresso in minuti
    procedura TEXT,
    immagine VARCHAR(255)
);

CREATE TABLE Ingrediente (
    idIngrediente INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE ComposizioneProdotto (
    idProdotto INT NOT NULL,
    idIngrediente INT NOT NULL,
    quantita VARCHAR(50) NOT NULL,
    PRIMARY KEY (idProdotto, idIngrediente),
    FOREIGN KEY (idProdotto) REFERENCES Prodotto(idProdotto) ON DELETE CASCADE,
    FOREIGN KEY (idIngrediente) REFERENCES Ingrediente(idIngrediente) ON DELETE CASCADE
);

-- 3. CARATTERISTICHE
CREATE TABLE GruppoMutuaEsclusione (
    idGmc INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(50) NOT NULL,
    descrizione VARCHAR(100)
);

CREATE TABLE Caratteristica (
    idCaratteristica INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(50) NOT NULL UNIQUE,
    descrizione VARCHAR(100),
    differenzaPrezzo DECIMAL(6,2) NOT NULL DEFAULT 0.00,
    isDefault BOOLEAN NOT NULL DEFAULT FALSE,
    idGmc INT,
    FOREIGN KEY (idGmc) REFERENCES GruppoMutuaEsclusione(idGmc) ON DELETE SET NULL
);

CREATE TABLE PossessoCaratteristica (
    idProdotto INT NOT NULL,
    idCaratteristica INT NOT NULL,
    PRIMARY KEY (idProdotto, idCaratteristica),
    FOREIGN KEY (idProdotto) REFERENCES Prodotto(idProdotto) ON DELETE CASCADE,
    FOREIGN KEY (idCaratteristica) REFERENCES Caratteristica(idCaratteristica) ON DELETE CASCADE
);

-- 4. ORDINI
CREATE TABLE Ordine (
    idOrdine INT AUTO_INCREMENT PRIMARY KEY,
    statoCorrente ENUM('inserito','in preparazione','pronto','in consegna', 'consegnato', 'annullato') DEFAULT "inserito",
    timeInserimento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    orarioConsegnaRichiesto DATETIME NOT NULL,
    tempoStimato INT NOT NULL, -- In minuti
    prezzoTot DECIMAL(6,2) NOT NULL,
    idCliente INT NOT NULL,
    FOREIGN KEY (idCliente) REFERENCES Cliente(idCliente) ON DELETE RESTRICT
);

CREATE TABLE DettaglioProdotto (
    idDettaglio INT AUTO_INCREMENT PRIMARY KEY,
    quantita INT NOT NULL DEFAULT 1,
    idOrdine INT NOT NULL,
    idProdotto INT NOT NULL,
    FOREIGN KEY (idOrdine) REFERENCES Ordine(idOrdine) ON DELETE CASCADE,
    FOREIGN KEY (idProdotto) REFERENCES Prodotto(idProdotto) ON DELETE RESTRICT
);

CREATE TABLE AssociazioneCaratteristica (
    idDettaglio INT NOT NULL,
    idCaratteristica INT NOT NULL,
    PRIMARY KEY (idDettaglio, idCaratteristica),
    FOREIGN KEY (idDettaglio) REFERENCES DettaglioProdotto(idDettaglio) ON DELETE CASCADE,
    FOREIGN KEY (idCaratteristica) REFERENCES Caratteristica(idCaratteristica) ON DELETE RESTRICT
);

CREATE TABLE CambioStato (
    idCambioStato INT AUTO_INCREMENT PRIMARY KEY,
    stato ENUM('inserito', 'in preparazione', 'pronto', 'in consegna', 'consegnato', 'annullato') NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    idOrdine INT NOT NULL,
    idPersonale INT,
    FOREIGN KEY (idOrdine) REFERENCES Ordine(idOrdine) ON DELETE CASCADE,
    FOREIGN KEY (idPersonale) REFERENCES Personale(idPersonale) ON DELETE RESTRICT
);