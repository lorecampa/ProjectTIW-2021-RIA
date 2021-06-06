-- MySQL dump 10.13  Distrib 8.0.21, for macos10.15 (x86_64)
--
-- Host: localhost    Database: MusicPlaylistDb
-- ------------------------------------------------------
-- Server version	8.0.24

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `Album`
--

DROP TABLE IF EXISTS `Album`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Album` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `interpreter` varchar(255) NOT NULL,
  `year` smallint NOT NULL,
  `genre` varchar(45) NOT NULL,
  `idCreator` int NOT NULL,
  `imageUrl` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniqueAlbum` (`title`,`interpreter`,`year`,`genre`,`idCreator`),
  KEY `user_idx` (`idCreator`),
  CONSTRAINT `user` FOREIGN KEY (`idCreator`) REFERENCES `User` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Album`
--

LOCK TABLES `Album` WRITE;
/*!40000 ALTER TABLE `Album` DISABLE KEYS */;
INSERT INTO `Album` VALUES (33,'Recovery','Eminem',2010,'Pop',17,'albumImage_33.jpg'),(34,'When We All Fall Asleep','Billie Elish',2020,'Pop',17,'albumImage_34.jpg'),(35,'Nightmare','Avenged Sevenfold',2012,'Rock',17,'albumImage_35.jpg'),(36,'Gemelli','Ernia',2020,'Pop',18,'albumImage_36.jpg');
/*!40000 ALTER TABLE `Album` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Match`
--

DROP TABLE IF EXISTS `Match`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Match` (
  `idSong` int NOT NULL,
  `idPlaylist` int NOT NULL,
  `dateAdding` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`idSong`,`idPlaylist`),
  KEY `playlistId_idx` (`idPlaylist`),
  CONSTRAINT `playlistId` FOREIGN KEY (`idPlaylist`) REFERENCES `Playlist` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `songId` FOREIGN KEY (`idSong`) REFERENCES `Song` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Match`
--

LOCK TABLES `Match` WRITE;
/*!40000 ALTER TABLE `Match` DISABLE KEYS */;
/*!40000 ALTER TABLE `Match` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `MatchOrder`
--

DROP TABLE IF EXISTS `MatchOrder`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `MatchOrder` (
  `idSong` int NOT NULL,
  `idPlaylist` int NOT NULL,
  `idSongBefore` int NOT NULL,
  `dateAdding` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`idSong`,`idPlaylist`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `MatchOrder`
--

LOCK TABLES `MatchOrder` WRITE;
/*!40000 ALTER TABLE `MatchOrder` DISABLE KEYS */;
/*!40000 ALTER TABLE `MatchOrder` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Playlist`
--

DROP TABLE IF EXISTS `Playlist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Playlist` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `idCreator` int NOT NULL,
  `dateCreation` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `playlistUnique` (`title`,`idCreator`),
  KEY `user_idx` (`idCreator`),
  CONSTRAINT `userPlaylist` FOREIGN KEY (`idCreator`) REFERENCES `User` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=64 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Playlist`
--

LOCK TABLES `Playlist` WRITE;
/*!40000 ALTER TABLE `Playlist` DISABLE KEYS */;
INSERT INTO `Playlist` VALUES (58,'Gym',17,'2021-06-04 22:19:30'),(59,'Running',17,'2021-06-04 22:19:36'),(60,'RockMusic',17,'2021-06-04 22:45:02'),(63,'School',18,'2021-06-06 14:59:01');
/*!40000 ALTER TABLE `Playlist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `Song`
--

DROP TABLE IF EXISTS `Song`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `Song` (
  `id` int NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL,
  `songUrl` varchar(255) DEFAULT NULL,
  `idAlbum` int NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniqueSong` (`title`,`idAlbum`),
  KEY `album_idx` (`idAlbum`),
  CONSTRAINT `album` FOREIGN KEY (`idAlbum`) REFERENCES `Album` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=83 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `Song`
--

LOCK TABLES `Song` WRITE;
/*!40000 ALTER TABLE `Song` DISABLE KEYS */;
INSERT INTO `Song` VALUES (66,'Darkness','songAudio_66_33.mp3',33),(67,'Loose yourself','songAudio_67_33.mp3',33),(68,'Godzilla','songAudio_68_33.mp3',33),(69,'Love the way you lie','songAudio_69_33.mp3',33),(70,'The Way i am','songAudio_70_33.mp3',33),(71,'Ocean eyes','songAudio_71_34.mp3',34),(72,'when the party\'s over','songAudio_72_34.mp3',34),(73,'party favor','songAudio_73_34.mp3',34),(74,'Hail to the king','songAudio_74_35.mp3',35),(75,'Nightmare','songAudio_75_35.mp3',35),(76,'Afterlife','songAudio_76_35.mp3',35),(77,'Bat Country','songAudio_77_35.mp3',35),(81,'Vivo','songAudio_81_36.mp3',36);
/*!40000 ALTER TABLE `Song` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `User`
--

DROP TABLE IF EXISTS `User`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `User` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `surname` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniqueEmail` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `User`
--

LOCK TABLES `User` WRITE;
/*!40000 ALTER TABLE `User` DISABLE KEYS */;
INSERT INTO `User` VALUES (17,'utenteA','utenteA@gmail.com','ciao','Utente','A'),(18,'utenteB','utenteB@gmail.com','ciao','Utente','B'),(19,'utenteC','utenteC@gmail.com','ciao','Utente','C');
/*!40000 ALTER TABLE `User` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-06-06 22:56:33
