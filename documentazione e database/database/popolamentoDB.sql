USE WebDelivery;

-- UTENTI
INSERT INTO Utente (email, password, nome, cognome, ruolo) VALUES 
('admin@webdelivery.it', 'hashedpassword1', 'Mario', 'Rossi', 'proprietario'),
('staff@webdelivery.it', 'hashedpassword2', 'Luigi', 'Verdi', 'personale'),
('maikol@cliente.it', 'hashedpassword3', 'Maikol', 'Gasparroni', 'cliente'),
('chiara@cliente.it', 'hashedpassword4', 'Chiara', 'Zanin', 'cliente');

INSERT INTO Proprietario (idUtente) VALUES (1);
INSERT INTO Personale (idUtente) VALUES (2);
INSERT INTO Cliente (idUtente, telefono, via, civico, citta) VALUES 
(3, '3331234567', 'Via Mazzini', '42', 'Alba Adriatica');
INSERT INTO Cliente (idUtente, telefono, via, civico, citta) VALUES 
(4, '3331234567', 'Via Roma', '12', 'Alba Adriatica');

-- MENU
INSERT INTO Prodotto (nome, descrizione, categoria, prezzoBase, tempoPreparazione, procedura) VALUES
('Pizza Margherita', 'La classica pizza', "pizze", 6.00, 15, 'Stendere impasto, aggiungere pomodoro e mozzarella, infornare.'),
('Caffè Espresso', 'Caffè 100% Arabica', "caffè", 1.20, 2, 'Estrarre il caffè dalla macchina per 25 secondi.');

INSERT INTO Ingrediente (nome) VALUES ('Farina'), ('Pomodoro'), ('Mozzarella'), ('Caffè in chicchi');

INSERT INTO ComposizioneProdotto (idProdotto, idIngrediente, quantita) VALUES
(1, 1, '200g'), (1, 2, '100g'), (1, 3, '100g'),
(2, 4, '7g');

-- CARATTERISTICHE (L'esempio del caffè citato nella specifica)
INSERT INTO GruppoMutuaEsclusione (nome, descrizione) VALUES ('Zucchero', 'Livello di zucchero nel caffè');

INSERT INTO Caratteristica (nome, differenzaPrezzo, isDefault, idGmc) VALUES
('Zuccherato', 0.00, TRUE, 1),
('Senza Zucchero', -0.05, FALSE, 1),
('Molto Zuccherato', 0.00, FALSE, 1),
('Con Panna', 0.50, FALSE, NULL); -- Libera, nessun Gmc

INSERT INTO PossessoCaratteristica (idProdotto, idCaratteristica) VALUES
(2, 1), (2, 2), (2, 3), (2, 4);