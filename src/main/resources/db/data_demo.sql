-- Comptes de démonstration : responsable_demo / admin, agent_demo / user.
insert into personne (login, mot_de_passe, nom, prenom, type_personne)
values
    ('responsable_demo', '$2a$10$gye0fQ/UhMMR9RNyKgVDAOhEPyVT7xIIu7WaZZGTil.sSqxVEeZF2', 'Démo', 'Responsable', 'RESPONSABLE'),
    ('agent_demo', '$2a$10$8Xyv74WeV6rVr7c8LZ30Gu2lVhKN.ZWPNoDjgZGU.kBFF5WNsDs3.', 'Démo', 'Agent', 'AGENT');

-- Donnees de demonstration pour une base initialisee avec create.sql.
-- A executer apres le schema et les comptes de demonstration.

INSERT INTO station (id, nom, adresse, latitude, longitude, ville)
VALUES
    (9001, 'Gare Centrale', '1 place de la Gare', 48.8566, 2.3522, 'Paris'),
    (9002, 'Gare du Parc', '12 avenue du Parc', 45.7640, 4.8357, 'Lyon'),
    (9003, 'Gare Maritime', '8 quai du Port', 43.2965, 5.3698, 'Marseille');

INSERT INTO prestataire (id, nom, num_telephone, email)
VALUES
    (9001, 'Ascenseurs Express', '0102030405', 'contact@ascenseurs-express.example'),
    (9002, 'Services Mobilite', '0607080910', 'support@services-mobilite.example');

INSERT INTO equipement
    (id, description, station_id, etat, categorie_equipement, type_equipement)
VALUES
    (9001, 'Ascenseur principal - quai 1', 9001, 'HS', 'ASCENSEUR', 'ASCENSEUR'),
    (9002, 'Distributeur de billets - hall', 9001, 'EN_MARCHE', 'DISTRIBUTEUR', 'DISTRIBUTEUR'),
    (9003, 'Ecran des horaires - entree', 9002, 'ETEINT', 'ECRAN_INFORMATION', 'ECRAN_INFORMATION'),
    (9004, 'Escalier mecanique - sortie nord', 9002, 'HS', 'ESCALIER', 'ESCALIER'),
    (9005, 'Portique d acces - voie 2', 9003, 'EN_MARCHE', 'PORTIQUE', 'PORTIQUE');

INSERT INTO incident
    (id, equipement_id, agent_id, prestataire_id, date_ouverture, description, statut, date_cloture)
VALUES
    (9001, 9001, (SELECT id FROM personne WHERE login = 'agent_demo' AND type_personne = 'AGENT'), NULL,
        CURRENT_TIMESTAMP - INTERVAL 8 HOUR, 'Ascenseur bloque au niveau du quai.', 'OUVERT', NULL),
    (9002, 9002, (SELECT id FROM personne WHERE login = 'agent_demo' AND type_personne = 'AGENT'), 9002,
        CURRENT_TIMESTAMP - INTERVAL 30 HOUR, 'Le distributeur refuse certaines cartes.', 'EN_COURS', NULL),
    (9003, 9003, (SELECT id FROM personne WHERE login = 'agent_demo' AND type_personne = 'AGENT'), 9002,
        CURRENT_TIMESTAMP - INTERVAL 4 DAY, 'Ecran eteint malgre plusieurs redemarrages.', 'CLOTURE',
        CURRENT_TIMESTAMP - INTERVAL 2 DAY),
    (9004, 9004, (SELECT id FROM personne WHERE login = 'agent_demo' AND type_personne = 'AGENT'), 9001,
        CURRENT_TIMESTAMP - INTERVAL 7 DAY, 'Arret de l escalier mecanique.', 'CLOTURE',
        CURRENT_TIMESTAMP - INTERVAL 3 DAY),
    (9005, 9005, (SELECT id FROM personne WHERE login = 'agent_demo' AND type_personne = 'AGENT'), NULL,
        CURRENT_TIMESTAMP - INTERVAL 2 HOUR, 'Le portique emet un signal sonore inhabituel.', 'OUVERT', NULL);

INSERT INTO evaluation (id, incident_id, note, commentaire, date)
VALUES
    (9001, 9003, 5, 'Intervention rapide et efficace.', CURRENT_TIMESTAMP - INTERVAL 1 DAY),
    (9002, 9004, 3, 'Reparation effectuee, quelques retards.', CURRENT_TIMESTAMP - INTERVAL 2 DAY);
