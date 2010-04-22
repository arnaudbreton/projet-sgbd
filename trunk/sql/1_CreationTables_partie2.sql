-- Remise à zéro des tables
DROP TABLE Stock;
DROP TABLE Compte_Bancaire_Fournisseurs;
DROP TABLE Compte_Bancaire_Clients;
DROP TABLE Commandes_Clients;
DROP TABLE Commandes_Fournisseurs;
DROP TABLE Fournisseurs;
DROP TABLE Clients;
DROP TABLE Produits;
DROP TABLE Categories_Produits;
DROP TABLE Types_produits;
DROP TABLE Taille;
DROP TABLE Couleur;
DROP TABLE Sexe;
DROP TABLE Adresse;


-- Remise à zéro des séquences
DROP SEQUENCE sq_taille;
DROP SEQUENCE sq_categories_produits;
DROP SEQUENCE sq_types_produits;
DROP SEQUENCE sq_sexe;
DROP SEQUENCE sq_couleur;
DROP SEQUENCE sq_produits;
DROP SEQUENCE sq_clients;
DROP SEQUENCE sq_fournisseurs;
DROP SEQUENCE sq_commandes_clients;
DROP SEQUENCE sq_commandes_fournisseurs;
DROP SEQUENCE sq_compte_bancaire_fournisseurs;
DROP SEQUENCE sq_compte_bancaire_clients;
DROP SEQUENCE sq_stock;

/* 
Ensemble des tables de l'entreprise BGB' (Bravo Gouin Breton),
spécialisée dans la vente de vêtements tendances pour hommes et femmes 
*/

-- Catégories des produits manipulés dans la base (pull, etc.)
CREATE TABLE Categories_Produits (
	id INTEGER PRIMARY KEY,
	libelle VARCHAR2(20) CONSTRAINT NOT_NULL_LIBELLE_CAT_PRODUIT NOT NULL
);

-- Types des produits manipulés dans la base (achetés ou vendus)
CREATE TABLE Types_Produits(
	id INTEGER PRIMARY KEY,
	libelle VARCHAR2(20) CONSTRAINT NOT_NULL_LIBELLE_TYPE_PRODUIT NOT NULL
);

CREATE TABLE Taille(
	id INTEGER PRIMARY KEY,
	libelle VARCHAR2(10) CONSTRAINT NOT_NULL_LIBELLE_TAILLE NOT NULL
);

CREATE TABLE Couleur(
	id INTEGER PRIMARY KEY,
	libelle VARCHAR2(20) CONSTRAINT NOT_NULL_LIBELLE_COULEUR NOT NULL
);

CREATE TABLE Sexe(
	id INTEGER PRIMARY KEY,
	libelle VARCHAR2(20) CONSTRAINT NOT_NULL_LIBELLE_SEXE NOT NULL
);

-- Ensemble des produits vendus ou achetés par l'entreprise
CREATE TABLE Produits(
	id INTEGER PRIMARY KEY,
	nom VARCHAR2(50) CONSTRAINT NOT_NULL_NOM_PRODUITS NOT NULL,
	taille INTEGER NOT NULL,
	couleur INTEGER NOT NULL,
	sexe INTEGER NOT NULL,
	prix NUMBER CONSTRAINT NOT_NULL_PRIX_PRODUIT NOT NULL,
	image VARCHAR2(50),
	descriptif VARCHAR2(100) CONSTRAINT NOT_NULL_DESCRIPTIF_PRODUIT NOT NULL,
	date_ajout DATE CONSTRAINT NOT_NULL_DATE_AJOUT_PRODUIT NOT NULL,
	quantite INTEGER CONSTRAINT NOT_NULL_QUANTITE_PRODUIT NOT NULL,
	categorie_produit INTEGER CONSTRAINT NOT_NULL_CATEGORIE_PRODUIT NOT NULL,
	type_produit INTEGER CONSTRAINT NOT_NULL_TYPE_PRODUIT NOT NULL,
	CONSTRAINT FK_PRODUITS_CATEGORIE_PRODUIT FOREIGN KEY(categorie_produit) REFERENCES Categories_Produits(id),
	CONSTRAINT FK_PRODUITS_TYPE_PRODUIT FOREIGN KEY(type_produit) REFERENCES Types_Produits(id),
	CONSTRAINT FK_PRODUITS_TAILLLE FOREIGN KEY(taille) REFERENCES Taille(id),
	CONSTRAINT FK_PRODUITS_COULEUR FOREIGN KEY(couleur) REFERENCES Couleur(id),
	CONSTRAINT FK_PRODUITS_SEXE FOREIGN KEY(sexe) REFERENCES Sexe(id),
	CONSTRAINT CHK_QUANTITE CHECK(quantite > 0)
);

-- Ensemble des mouvements de stock, permettant d'avoir un historique des achats / ventes
CREATE TABLE Stock(
	id INTEGER PRIMARY KEY,
	idProd INTEGER,
	date_mvt DATE CONSTRAINT NOT_NULL_DATE_MVT NOT NULL,
	mvt INTEGER CONSTRAINT NOT_NULL_MVT NOT NULL,
	CONSTRAINT FK_STOCK_PRODUITS FOREIGN KEY(idProd) REFERENCES Produits(id)
);

-- Adresse commune au client / fournisseur
CREATE TABLE Adresse (
	id INTEGER,
	numRue INTEGER,
	rue INTEGER,
	ville VARCHAR2(50),
	code_postal INTEGER CONSTRAINT CODE_POSTAL_NOT_NULL NOT NULL,
	telephone INTEGER CONSTRAINT NUM_TELEPHONE_NOT_NULL NOT NULL,
	pays VARCHAR2(50) CONSTRAINT PAYS_NOT_NULL NOT NULL,
	CONSTRAINT PK_ADRESSE PRIMARY KEY(id)
);

-- Ensemble des clients de la société
CREATE TABLE Clients (
	login VARCHAR2(50) PRIMARY KEY,
	mot_de_passe VARCHAR2(50) CONSTRAINT NOT_NULL_MOT_PASSE NOT NULL,
	nom VARCHAR2(50) CONSTRAINT NOM_CLIENT_NOT_NULL NOT NULL,
	prenom VARCHAR2(50) CONSTRAINT PRENOM_CLIENT_NOT_NULL NOT NULL,
	idAdresse INTEGER,
	CONSTRAINT FK_CLIENTS_ADRESSE FOREIGN KEY(idAdresse) REFERENCES Adresse(id)
);

-- Ensemble des fournisseurs de la société
CREATE TABLE Fournisseurs (
	siren INTEGER PRIMARY KEY,
	nom VARCHAR2(50) CONSTRAINT NOM_FOURNISSEUR_NOT_NULL NOT NULL,
	idAdresse INTEGER,
	CONSTRAINT FK_FOURNISSEUR_ADRESSE FOREIGN KEY(idAdresse) REFERENCES Adresse(id)
);


-- Commande que recoit l'entreprise
CREATE TABLE Commandes_Clients(
	id INTEGER PRIMARY KEY,
	loginClient VARCHAR2(50),
	idProduit INTEGER,
	date_creation DATE CONSTRAINT NOT_NULL_DATE_CREATION_CMD_CLI NOT NULL,
	date_envoi DATE,
	date_reception DATE,
	date_paiement DATE,	
	quantite INTEGER CONSTRAINT NOT_NULL_QUANTITE_COMMANDE_CLI NOT NULL ,
	prix_unitaire NUMBER NOT NULL,
	CONSTRAINT FK_COMMANDE_ID_CLI FOREIGN KEY(loginClient) REFERENCES Clients(login),
	CONSTRAINT FK_COMMANDE_PRODUIT_CLI FOREIGN KEY(idProduit) REFERENCES Produits(id),
	CONSTRAINT CHK_QTE_POSITIVE_CLI CHECK(quantite > 0),
	CONSTRAINT CHK_PRIX_UNITAIRE_POSITIF_CLI CHECK(prix_unitaire > 0),
	CONSTRAINT CHK_DATE_CREATION_PREMIERE_CLI CHECK(date_creation < date_paiement),
	CONSTRAINT CHK_DATE_PAIEMENT_SECONDE_CLI CHECK(date_paiement < date_envoi),
	CONSTRAINT CHK_DATE_ENVOI_TROISIEME_CLI CHECK(date_envoi < date_reception)
);

-- Commandes que passe l'entreprise
CREATE TABLE Commandes_Fournisseurs(
	id INTEGER PRIMARY KEY,
	idFournisseur INTEGER,
	idProduit INTEGER,
	date_creation DATE CONSTRAINT NOT_NULL_DATE_CREATION_CMD_FRS NOT NULL,
	date_envoi DATE,
	date_reception DATE,
	date_paiement DATE,	
	quantite INTEGER CONSTRAINT NOT_NULL_QUANTITE_COMMANDE_FRS NOT NULL ,
	prix_unitaire NUMBER NOT NULL,
	CONSTRAINT FK_COMMANDE_ID_FRS FOREIGN KEY(idFournisseur) REFERENCES Fournisseurs(siren),
	CONSTRAINT FK_COMMANDE_PRODUIT_FRS FOREIGN KEY(idProduit) REFERENCES Produits(id),
	CONSTRAINT CHK_QTE_POSITIVE_FRS CHECK(quantite > 0),
	CONSTRAINT CHK_PRIX_UNITAIRE_POSITIF_FRS CHECK(prix_unitaire > 0),
	CONSTRAINT CHK_DATE_CREATION_PREMIERE_FRS CHECK(date_creation < date_paiement),
	CONSTRAINT CHK_DATE_PAIEMENT_SECONDE_FRS CHECK(date_paiement < date_envoi),
	CONSTRAINT CHK_DATE_RECEP_TROISIEME_FRS CHECK(date_envoi < date_reception)
);

CREATE TABLE Compte_Bancaire_Fournisseurs(
	id INTEGER PRIMARY KEY,
	idCommandeFrs INTEGER,
	date_operation DATE NOT NULL,
	montant NUMBER NOT NULL,
	CONSTRAINT FK_COMMANDE_MVT_BANCAIRE_FRS FOREIGN KEY(idCommandeFrs) REFERENCES Commandes_Fournisseurs(id)
);

CREATE TABLE Compte_Bancaire_Clients(
	id INTEGER PRIMARY KEY,
	idCommandeCli INTEGER,
	date_operation DATE NOT NULL,
	montant NUMBER NOT NULL,
	CONSTRAINT FK_COMMANDE_MVT_BANCAIRE_CLI FOREIGN KEY(idCommandeCli) REFERENCES Commandes_Clients(id)
);

--Séquence de la clé primaire de stock
CREATE SEQUENCE sq_stock
	MINVALUE 1
	MAXVALUE 99999
	START WITH 1
	INCREMENT BY 1;

--Séquence de la clé primaire de commande
CREATE SEQUENCE sq_commandes_clients
	MINVALUE 1
	MAXVALUE 99999
	START WITH 1
	INCREMENT BY 1;
	
CREATE SEQUENCE sq_commandes_fournisseurs
	MINVALUE 1
	MAXVALUE 99999
	START WITH 1
	INCREMENT BY 1;
	
--Séquence de la clé primaire de couleur
CREATE SEQUENCE sq_couleur
	MINVALUE 1
	MAXVALUE 99999
	START WITH 1
	INCREMENT BY 1;

--Préparation des couleurs
INSERT INTO Couleur (id,libelle) VALUES (sq_couleur.NEXTVAL,'Rose');
INSERT INTO Couleur (id,libelle) VALUES (sq_couleur.NEXTVAL,'Bleu turquoise');
INSERT INTO Couleur (id,libelle) VALUES (sq_couleur.NEXTVAL,'Rouge');
INSERT INTO Couleur (id,libelle) VALUES (sq_couleur.NEXTVAL,'Vert');
INSERT INTO Couleur (id,libelle) VALUES (sq_couleur.NEXTVAL,'Gris');
INSERT INTO Couleur (id,libelle) VALUES (sq_couleur.NEXTVAL,'Noir');
INSERT INTO Couleur (id,libelle) VALUES (sq_couleur.NEXTVAL,'Marron');

--Séquence de la clé primaire de taille
CREATE SEQUENCE sq_taille
	MINVALUE 1
	MAXVALUE 99999
	START WITH 1
	INCREMENT BY 1;

-- Préparation des Tailles de vêtements
INSERT INTO Taille(id,libelle) VALUES (sq_taille.NEXTVAL,'XS');
INSERT INTO Taille(id,libelle) VALUES (sq_taille.NEXTVAL,'S');
INSERT INTO Taille(id,libelle) VALUES (sq_taille.NEXTVAL,'M');
INSERT INTO Taille(id,libelle) VALUES (sq_taille.NEXTVAL,'L');
INSERT INTO Taille(id,libelle) VALUES (sq_taille.NEXTVAL,'XL');
INSERT INTO Taille(id,libelle) VALUES (sq_taille.NEXTVAL,'XXL');

--Séquence de la clé primaire de Type_Produits
CREATE SEQUENCE sq_categories_produits
	MINVALUE 1
	MAXVALUE 99999
	START WITH 1
	INCREMENT BY 1;

-- Préparation des catégories de vêtements
INSERT INTO categories_Produits (id,libelle) VALUES(sq_categories_produits.NEXTVAL,'POLO');
INSERT INTO categories_Produits (id,libelle) VALUES(sq_categories_produits.NEXTVAL,'CHEMISE');
INSERT INTO categories_Produits (id,libelle) VALUES(sq_categories_produits.NEXTVAL,'COSTUME');
INSERT INTO categories_Produits (id,libelle) VALUES(sq_categories_produits.NEXTVAL,'PANTALON');
INSERT INTO categories_Produits (id,libelle) VALUES(sq_categories_produits.NEXTVAL,'CHAUSSURES');
INSERT INTO categories_Produits (id,libelle) VALUES(sq_categories_produits.NEXTVAL,'TSHIRT');
INSERT INTO categories_Produits (id,libelle) VALUES(sq_categories_produits.NEXTVAL,'PULL');
INSERT INTO categories_Produits (id,libelle) VALUES(sq_categories_produits.NEXTVAL,'JUPE');
INSERT INTO categories_Produits (id,libelle) VALUES(sq_categories_produits.NEXTVAL,'ROBE');

-- Séquence de la clé primaire de types_produits
CREATE SEQUENCE sq_types_produits
	MINVALUE 1
	MAXVALUE 99999
	START WITH 1
	INCREMENT BY 1;
	
-- Préparation des types de produits
INSERT INTO types_produits(id, libelle) VALUES(sq_types_produits.NEXTVAL, 'ACHETE');
INSERT INTO types_produits(id, libelle) VALUES(sq_types_produits.NEXTVAL, 'VENDU');

--Séquence de la clé primaire de sexe
CREATE SEQUENCE sq_sexe
	MINVALUE 1
	MAXVALUE 99999
	START WITH 1
	INCREMENT BY 1;

-- Préparation du sexe des vêtements
INSERT INTO Sexe (id,libelle) VALUES(sq_sexe.NEXTVAL,'HOMME');
INSERT INTO Sexe (id,libelle) VALUES(sq_sexe.NEXTVAL,'FEMME');
INSERT INTO Sexe (id,libelle) VALUES(sq_sexe.NEXTVAL,'MIXTE');

--Séquence de la clé primaire de produits
CREATE SEQUENCE sq_produits
	MINVALUE 1
	MAXVALUE 99999
	START WITH 1
	INCREMENT BY 1;

--Préparation des vêtements
INSERT INTO Produits (id,nom,taille,couleur,sexe,prix,descriptif,date_ajout,quantite,categorie_produit,type_produit)
VALUES (sq_produits.NEXTVAL,'Popeline',2,1,2,20.00,'Chemise longue en lin',SYSDATE,50,2,1);
INSERT INTO Produits (id,nom,taille,couleur,sexe,prix,descriptif,date_ajout,quantite,categorie_produit,type_produit)
VALUES (sq_produits.NEXTVAL,'Ellos',3,4,2,33.00,'Robe fluide stretch',SYSDATE,30,9,1);
INSERT INTO Produits (id,nom,taille,couleur,sexe,prix,descriptif,date_ajout,quantite,categorie_produit,type_produit)
VALUES (sq_produits.NEXTVAL,'Tipster',4,3,2,40.30,'Mini jupe battle',SYSDATE,20,8,2);
INSERT INTO Produits (id,nom,taille,couleur,sexe,prix,descriptif,date_ajout,quantite,categorie_produit,type_produit)
VALUES (sq_produits.NEXTVAL,'Kahia',1,2,1,49.00,'Polo maille jersey manches courtes',SYSDATE,35,1,2);
INSERT INTO Produits (id,nom,taille,couleur,sexe,prix,descriptif,date_ajout,quantite,categorie_produit,type_produit)
VALUES (sq_produits.NEXTVAL,'Dockers',3,5,1,90.00,'Pantalon coton leger',SYSDATE,55,4,2);


--Séquence de la clé primaire de clients
CREATE SEQUENCE sq_clients
	MINVALUE 1
	MAXVALUE 99999
	START WITH 1
	INCREMENT BY 1;

--Séquence de la clé primaire de fournisseurs
CREATE SEQUENCE sq_fournisseurs
	MINVALUE 1
	MAXVALUE 99999
	START WITH 1
	INCREMENT BY 1;
	
--Séquence de la clé primaire de compte_bancaire_fournisseurs
CREATE SEQUENCE sq_compte_bancaire_fournisseurs
	MINVALUE 1
	MAXVALUE 99999
	START WITH 1
	INCREMENT BY 1;
	
--Séquence de la clé primaire de compte_bancaire_fournisseurs
CREATE SEQUENCE sq_compte_bancaire_clients
	MINVALUE 1
	MAXVALUE 99999
	START WITH 1
	INCREMENT BY 1;