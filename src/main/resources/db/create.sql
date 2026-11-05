create table personne
(
    id            int unsigned auto_increment
        primary key,
    login         varchar(100) not null,
    mot_de_passe  mediumtext   not null,
    nom           varchar(100) not null,
    prenom        varchar(100) not null,
    type_personne varchar(20)  not null,
    constraint Personne_LOGIN
        unique (login)
)
    charset = utf16;

create table prestataire
(
    id            int unsigned auto_increment
        primary key,
    nom           varchar(100) not null,
    num_telephone varchar(16)  not null,
    email         varchar(100) not null
)
    charset = utf16;

create table station
(
    id        int unsigned auto_increment
        primary key,
    nom       varchar(100) not null,
    adresse   varchar(100) not null,
    latitude  double       not null,
    longitude double       not null,
    ville     varchar(100) not null
)
    charset = utf16;

create table equipement
(
    id                   int unsigned auto_increment
        primary key,
    description          varchar(200) null,
    station_id           int unsigned not null,
    etat                 varchar(16)  null,
    categorie_equipement varchar(30)  not null,
    type_equipement      varchar(40)  not null,
    constraint Equipement_Station_FK
        foreign key (station_id) references station (id)
)
    charset = utf16;

create table incident
(
    id             int unsigned auto_increment
        primary key,
    equipement_id  int unsigned  not null,
    agent_id       int unsigned  not null,
    prestataire_id int unsigned  null,
    date_ouverture datetime      not null,
    description    varchar(2000) not null,
    statut         varchar(100)  not null,
    date_cloture   datetime      null,
    constraint incident_pk
        unique (id),
    constraint Incident_Equipement_FK
        foreign key (equipement_id) references equipement (id),
    constraint Incident_Personne_FK
        foreign key (agent_id) references personne (id),
    constraint Incident_Prestataire_FK
        foreign key (prestataire_id) references prestataire (id)
)
    charset = utf16;

create table evaluation
(
    id          int unsigned auto_increment
        primary key,
    incident_id int unsigned  not null,
    note        int unsigned  not null,
    commentaire varchar(1000) null,
    date        datetime      not null,
    constraint Evaluation_Incident_FK
        foreign key (incident_id) references incident (id)
)
    charset = utf16;

create index Incident_Type_Equipement_FK
    on incident (equipement_id);

CREATE TABLE incident_event (
                                id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                incident_id INT UNSIGNED NOT NULL,
                                type_evenement VARCHAR(50) NOT NULL,
                                description VARCHAR(1000) NOT NULL,
                                date_evenement DATETIME NOT NULL,
                                agent_id INT UNSIGNED NOT NULL,
                                CONSTRAINT fk_incident_event_incident
                                    FOREIGN KEY (incident_id) REFERENCES incident(id),
                                CONSTRAINT fk_incident_event_agent
                                    FOREIGN KEY (agent_id) REFERENCES personne(id)
);

CREATE INDEX idx_incident_event_incident
    ON incident_event(incident_id);

CREATE INDEX idx_incident_event_date
    ON incident_event(date_evenement);