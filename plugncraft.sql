# ************************************************************
# Sequel Pro SQL dump
# Version 4541
#
# http://www.sequelpro.com/
# https://github.com/sequelpro/sequelpro
#
# Host: 192.168.0.5 (MySQL 5.7.23-0ubuntu0.18.04.1)
# Database: plugncraft
# Generation Time: 2018-08-19 21:25:51 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table ai_answers
# ------------------------------------------------------------

DROP TABLE IF EXISTS `ai_answers`;

CREATE TABLE `ai_answers` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `keywords` text NOT NULL,
  `answer` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

LOCK TABLES `ai_answers` WRITE;
/*!40000 ALTER TABLE `ai_answers` DISABLE KEYS */;

INSERT INTO `ai_answers` (`id`, `keywords`, `answer`)
VALUES
	(1,'affiche afficher aide l\'aide show help','Je suis PlugNCraft ! Vous pouvez me demander :\n\n`Dis PlugNCraft, bonjour` juste histoire de me dire bonjour\n`Dis PlugNCraft, démarre un serveur` pour démarrer un serveur PlugNCraft\n`Dis PlugNCraft, donne moi le statut des serveurs` pour voir le statut des serveurs de PlugNCraft\n\nC\'est tout pour le moment, mais des nouveautés arrivent bientôt !'),
	(2,'bonjour salut bonne journée après midi soirée','Salut @player !'),
	(3,'démarre demarre serveur','startServer()'),
	(4,'donne le stats status statut des serveurs','status()');

/*!40000 ALTER TABLE `ai_answers` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table games
# ------------------------------------------------------------

DROP TABLE IF EXISTS `games`;

CREATE TABLE `games` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` text NOT NULL,
  `min_players` int(11) NOT NULL,
  `max_players` int(11) NOT NULL,
  `slot` int(11) NOT NULL,
  `icon` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

LOCK TABLES `games` WRITE;
/*!40000 ALTER TABLE `games` DISABLE KEYS */;

INSERT INTO `games` (`id`, `name`, `description`, `min_players`, `max_players`, `slot`, `icon`)
VALUES
	('hub','Hub','PlugNCraft est un serveur en développement, modèle de l\'inclusion à CraftSearch. Il est basé sur un système de serveurs automatiques qui démarrent en fonction de besoins des joueurs.',101,100,-1,'WOOL'),
	('hungergames','HungerGames','Après un décompte le combat commence! A armes égales, seul le meilleur remportera la victoire.\n\nMini jeu par ZabriCraft',2,24,12,'IRON_SWORD'),
	('replica','Replica','Le Replica est un mini-jeu dont le but est de reproduire l\'image sur le tableau face à vous le plus rapidement possible.\n\nMini jeu par ZabriCraft',2,10,10,'STAINED_CLAY');

/*!40000 ALTER TABLE `games` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table maps
# ------------------------------------------------------------

DROP TABLE IF EXISTS `maps`;

CREATE TABLE `maps` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `game` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` text NOT NULL,
  `map_hub` varchar(255) NOT NULL,
  `map_game` varchar(255) NOT NULL,
  `icon` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

LOCK TABLES `maps` WRITE;
/*!40000 ALTER TABLE `maps` DISABLE KEYS */;

INSERT INTO `maps` (`id`, `game`, `name`, `description`, `map_hub`, `map_game`, `icon`)
VALUES
	(1,'hub','Hub','Hub','hub','game','WOOL'),
	(2,'replica','Replica','Map par défaut du Replica','hub','game','STAINED_CLAY'),
	(3,'hungergames','HungerGames','Map par défaut du HungerGames','hub','game','IRON_SWORD');

/*!40000 ALTER TABLE `maps` ENABLE KEYS */;
UNLOCK TABLES;


# Dump of table players
# ------------------------------------------------------------

DROP TABLE IF EXISTS `players`;

CREATE TABLE `players` (
  `uuid` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `grade` varchar(255) NOT NULL,
  `first_login` datetime NOT NULL,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


# Dump of table servers
# ------------------------------------------------------------

DROP TABLE IF EXISTS `servers`;

CREATE TABLE `servers` (
  `id` varchar(255) NOT NULL,
  `type` varchar(255) NOT NULL,
  `port` int(11) NOT NULL,
  `status` varchar(255) NOT NULL,
  `map` int(11) NOT NULL,
  `players` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


# Dump of table start
# ------------------------------------------------------------

DROP TABLE IF EXISTS `start`;

CREATE TABLE `start` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `game` varchar(255) NOT NULL,
  `map` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;




/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
