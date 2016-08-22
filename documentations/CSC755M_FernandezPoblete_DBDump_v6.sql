CREATE DATABASE  IF NOT EXISTS `db_genesched` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `db_genesched`;
-- MySQL dump 10.13  Distrib 5.7.12, for Win64 (x86_64)
--
-- Host: localhost    Database: db_genesched
-- ------------------------------------------------------
-- Server version	5.7.13-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `gs_activity`
--

DROP TABLE IF EXISTS `gs_activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gs_activity` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sessionID` int(11) NOT NULL,
  `venueID` int(11) NOT NULL,
  `name` varchar(60) NOT NULL,
  `length` int(11) NOT NULL,
  `days` char(13) NOT NULL,
  `startTimeRange` time DEFAULT NULL,
  `endTimeRange` time DEFAULT NULL,
  `assignedTime` datetime DEFAULT NULL,
  `dateAdded` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `activityfk_1` (`sessionID`),
  KEY `activityfk_2` (`venueID`),
  CONSTRAINT `activityfk_1` FOREIGN KEY (`sessionID`) REFERENCES `gs_session` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `activityfk_2` FOREIGN KEY (`venueID`) REFERENCES `gs_venue` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gs_activity`
--

LOCK TABLES `gs_activity` WRITE;
/*!40000 ALTER TABLE `gs_activity` DISABLE KEYS */;
INSERT INTO `gs_activity` VALUES (1,2,3,'Midterm Study Group',120,'0,0,0,0,0,1,0','09:00:00','16:00:00','2016-07-08 09:45:00','2016-08-16 04:31:55',1),(3,2,2,'LSCS GA',180,'0,1,1,1,0,1,0','12:30:00','18:00:00','2016-08-05 12:30:00','2016-08-16 04:40:53',1),(4,2,1,'Git Seminar',180,'0,0,1,0,0,0,0','13:00:00','18:00:00','2016-07-05 14:45:00','2016-08-16 04:46:36',1),(5,2,5,'Photshop Seminar',150,'0,0,0,0,0,0,0','10:00:00','18:00:00','2016-08-16 10:15:00','2016-08-16 04:59:21',1),(6,2,4,'CCS Zumba',90,'0,1,1,1,0,0,0','07:30:00','12:00:00','2016-06-21 10:15:00','2016-08-17 01:21:12',1);
/*!40000 ALTER TABLE `gs_activity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gs_activity_date`
--

DROP TABLE IF EXISTS `gs_activity_date`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gs_activity_date` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `actID` int(11) NOT NULL,
  `actDate` date DEFAULT NULL,
  `dateAdded` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `actdatefk_1` (`actID`),
  CONSTRAINT `actdatefk_1` FOREIGN KEY (`actID`) REFERENCES `gs_activity` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gs_activity_date`
--

LOCK TABLES `gs_activity_date` WRITE;
/*!40000 ALTER TABLE `gs_activity_date` DISABLE KEYS */;
INSERT INTO `gs_activity_date` VALUES (1,5,'2016-08-16','2016-08-16 04:59:21',1),(2,5,'2016-08-17','2016-08-16 04:59:21',1);
/*!40000 ALTER TABLE `gs_activity_date` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gs_activity_target_group`
--

DROP TABLE IF EXISTS `gs_activity_target_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gs_activity_target_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `groupID` int(11) NOT NULL,
  `actID` int(11) NOT NULL,
  `dateAdded` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `actTGfk_1_idx` (`groupID`),
  KEY `actTGfk_2_idx` (`actID`),
  CONSTRAINT `actTGfk_1` FOREIGN KEY (`groupID`) REFERENCES `gs_target_group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `actTGfk_2` FOREIGN KEY (`actID`) REFERENCES `gs_activity` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=83 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gs_activity_target_group`
--

LOCK TABLES `gs_activity_target_group` WRITE;
/*!40000 ALTER TABLE `gs_activity_target_group` DISABLE KEYS */;
INSERT INTO `gs_activity_target_group` VALUES (1,5,5,'2016-08-16 04:59:21',1),(2,10,5,'2016-08-16 04:59:21',1),(3,15,5,'2016-08-16 04:59:21',1),(4,1,1,'2016-08-16 13:06:17',1),(5,2,1,'2016-08-16 13:06:17',1),(6,3,1,'2016-08-16 13:06:17',1),(7,4,1,'2016-08-16 13:06:17',1),(8,5,1,'2016-08-16 13:06:17',1),(9,6,1,'2016-08-16 13:06:17',1),(10,7,1,'2016-08-16 13:06:17',1),(11,8,1,'2016-08-16 13:06:17',1),(12,9,1,'2016-08-16 13:06:17',1),(13,10,1,'2016-08-16 13:06:17',1),(14,11,1,'2016-08-16 13:06:17',1),(15,12,1,'2016-08-16 13:06:17',1),(16,13,1,'2016-08-16 13:06:17',1),(17,14,1,'2016-08-16 13:06:17',1),(18,15,1,'2016-08-16 13:06:17',1),(19,16,1,'2016-08-16 13:06:17',1),(20,17,1,'2016-08-16 13:06:17',1),(21,18,1,'2016-08-16 13:06:17',1),(22,19,1,'2016-08-16 13:06:17',1),(42,1,3,'2016-08-16 13:07:02',1),(43,2,3,'2016-08-16 13:07:02',1),(44,3,3,'2016-08-16 13:07:02',1),(45,4,3,'2016-08-16 13:07:02',1),(46,5,3,'2016-08-16 13:07:02',1),(47,6,3,'2016-08-16 13:07:02',1),(48,7,3,'2016-08-16 13:07:02',1),(49,8,3,'2016-08-16 13:07:02',1),(50,9,3,'2016-08-16 13:07:02',1),(51,10,3,'2016-08-16 13:07:02',1),(52,11,3,'2016-08-16 13:07:02',1),(53,12,3,'2016-08-16 13:07:02',1),(54,13,3,'2016-08-16 13:07:02',1),(55,14,3,'2016-08-16 13:07:02',1),(56,15,3,'2016-08-16 13:07:02',1),(57,16,3,'2016-08-16 13:07:02',1),(58,17,3,'2016-08-16 13:07:02',1),(59,18,3,'2016-08-16 13:07:02',1),(60,19,3,'2016-08-16 13:07:02',1),(61,5,4,'2016-08-16 13:07:42',1),(62,10,4,'2016-08-16 13:07:42',1),(63,15,4,'2016-08-16 13:07:42',1),(64,1,6,'2016-08-17 01:21:12',1),(65,2,6,'2016-08-17 01:21:12',1),(66,3,6,'2016-08-17 01:21:12',1),(67,4,6,'2016-08-17 01:21:12',1),(68,5,6,'2016-08-17 01:21:12',1),(69,6,6,'2016-08-17 01:21:12',1),(70,7,6,'2016-08-17 01:21:12',1),(71,8,6,'2016-08-17 01:21:12',1),(72,9,6,'2016-08-17 01:21:12',1),(73,10,6,'2016-08-17 01:21:12',1),(74,11,6,'2016-08-17 01:21:12',1),(75,12,6,'2016-08-17 01:21:12',1),(76,13,6,'2016-08-17 01:21:12',1),(77,14,6,'2016-08-17 01:21:12',1),(78,15,6,'2016-08-17 01:21:12',1),(79,16,6,'2016-08-17 01:21:12',1),(80,17,6,'2016-08-17 01:21:12',1),(81,18,6,'2016-08-17 01:21:12',1),(82,19,6,'2016-08-17 01:21:12',1);
/*!40000 ALTER TABLE `gs_activity_target_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gs_audit`
--

DROP TABLE IF EXISTS `gs_audit`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gs_audit` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `activity` text NOT NULL,
  `status` tinyint(1) NOT NULL DEFAULT '1',
  `dateAdded` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=170 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gs_audit`
--

LOCK TABLES `gs_audit` WRITE;
/*!40000 ALTER TABLE `gs_audit` DISABLE KEYS */;
INSERT INTO `gs_audit` VALUES (1,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-12 14:42:35'),(2,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 logged out.',1,'2016-08-12 14:42:36'),(3,'Anonymous user with ip address 0:0:0:0:0:0:0:1 failed to login to accountryanaustinf.',1,'2016-08-12 14:42:43'),(4,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 logged in.',1,'2016-08-12 14:42:51'),(5,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-12 16:06:38'),(6,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 logged in.',1,'2016-08-13 04:39:07'),(7,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 logged in.',1,'2016-08-13 13:44:01'),(8,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-13 13:48:21'),(9,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 logged out.',1,'2016-08-13 13:48:21'),(10,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 logged in.',1,'2016-08-13 13:48:27'),(11,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-14 00:36:45'),(12,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-15 11:56:20'),(13,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-15 12:05:37'),(14,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-15 12:06:03'),(15,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-15 12:08:26'),(16,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-15 12:11:33'),(17,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 had an invalid token on Get Session.',1,'2016-08-15 12:11:34'),(18,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-15 12:13:17'),(19,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 had an invalid token on Get Session.',1,'2016-08-15 12:13:18'),(20,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-15 12:16:35'),(21,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 had an invalid token on Get Session.',1,'2016-08-15 12:16:36'),(22,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-15 12:17:29'),(23,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 had an invalid token on Get Session.',1,'2016-08-15 12:17:30'),(24,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-15 12:28:53'),(25,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-15 12:29:23'),(26,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 had an invalid token on Get Session.',1,'2016-08-15 12:29:24'),(27,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-15 12:30:33'),(28,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-15 12:30:33'),(29,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 had an invalid token on Get Session.',1,'2016-08-15 12:30:34'),(30,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-15 12:32:18'),(31,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 had an invalid token on Get Session.',1,'2016-08-15 12:32:20'),(32,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-15 12:34:34'),(33,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 had an invalid token on Get Session.',1,'2016-08-15 12:34:35'),(34,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-15 12:38:09'),(35,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 had an invalid token on Get Session.',1,'2016-08-15 12:38:10'),(36,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-15 12:48:23'),(37,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 had an invalid token on Get Session.',1,'2016-08-15 12:48:25'),(38,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 ran into data validation errors on edit session.',1,'2016-08-15 12:50:26'),(39,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-15 12:53:49'),(40,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 ran into data validation errors on edit session.',1,'2016-08-15 12:53:49'),(41,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-15 12:55:07'),(42,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-15 12:55:07'),(43,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 ran into data validation errors on edit session.',1,'2016-08-15 12:55:07'),(44,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 ran into data validation errors on edit session.',1,'2016-08-15 12:55:35'),(45,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 ran into data validation errors on edit session.',1,'2016-08-15 12:56:05'),(46,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-15 12:56:52'),(47,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 ran into data validation errors on edit session.',1,'2016-08-15 12:56:53'),(48,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 ran into data validation errors on edit session.',1,'2016-08-15 12:57:59'),(49,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 ran into data validation errors on edit session.',1,'2016-08-15 13:00:03'),(50,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 ran into data validation errors on edit session.',1,'2016-08-15 13:01:15'),(51,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 ran into data validation errors on edit session.',1,'2016-08-15 13:01:59'),(52,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-15 13:04:08'),(53,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-15 13:04:36'),(54,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-15 13:05:23'),(55,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 edited session 2: Term 3 AY 2015-2016.',1,'2016-08-15 13:05:23'),(56,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 edited session 2: Term 3 AY 2015-2016.',1,'2016-08-15 13:09:20'),(57,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-15 13:10:28'),(58,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 had an invalid token on Get Session.',1,'2016-08-15 13:10:29'),(59,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 edited session 2: Term 3 AY 2015-2016.',1,'2016-08-15 13:11:01'),(60,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 edited session 2: Term 3 AY 2015-2016.',1,'2016-08-15 13:11:21'),(61,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-15 13:15:21'),(62,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 edited session 1: Test Session.',1,'2016-08-15 13:15:21'),(63,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 edited session 2: Term 3 AY 2015-2016.',1,'2016-08-15 13:15:53'),(64,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-16 03:16:53'),(65,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 edited session 2: Term 3 AY 2015-2016.',1,'2016-08-16 03:16:53'),(66,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 set their session to session 2.',1,'2016-08-16 03:18:39'),(67,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 ran into the error No value specified for parameter 1',1,'2016-08-16 03:18:40'),(68,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-16 03:20:57'),(69,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 set their session to session 2.',1,'2016-08-16 03:20:57'),(70,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-16 04:05:48'),(71,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-16 04:19:06'),(72,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-16 04:19:06'),(73,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-16 04:28:43'),(74,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 had an invalid token on Add Activity.',1,'2016-08-16 04:28:46'),(75,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-16 04:31:26'),(76,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 added activity Midterm Study Group.',1,'2016-08-16 04:31:55'),(77,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 ran into the error Unknown column \'assignedTime\' in \'field list\'',1,'2016-08-16 04:31:55'),(78,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 ran into the error Duplicate entry \'Midterm Study Group\' for key \'name\'',1,'2016-08-16 04:33:48'),(79,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-16 04:36:36'),(80,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-16 04:37:28'),(81,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-16 04:38:20'),(82,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-16 04:39:57'),(83,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 added activity LSCS GA.',1,'2016-08-16 04:40:53'),(84,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-16 04:45:38'),(85,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 had an invalid token on Add Activity.',1,'2016-08-16 04:46:14'),(86,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 added activity Git Seminar.',1,'2016-08-16 04:46:36'),(87,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 added venue G306.',1,'2016-08-16 04:47:40'),(88,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 ran into the error Can not issue data manipulation statements with executeQuery().',1,'2016-08-16 04:48:14'),(89,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-16 04:49:08'),(90,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 ran into the error Duplicate entry \'Photshop Seminar\' for key \'name\'',1,'2016-08-16 04:49:08'),(91,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 ran into the error Can not issue data manipulation statements with executeQuery().',1,'2016-08-16 04:50:03'),(92,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-16 04:50:32'),(93,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 ran into the error Duplicate entry \'2016-08-17\' for key \'actDate\'',1,'2016-08-16 04:50:33'),(94,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 ran into the error Field \'actDate\' doesn\'t have a default value',1,'2016-08-16 04:51:29'),(95,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 ran into the error Field \'actDate\' doesn\'t have a default value',1,'2016-08-16 04:51:52'),(96,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 ran into the error Field \'actDate\' doesn\'t have a default value',1,'2016-08-16 04:52:34'),(97,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-16 04:53:32'),(98,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 ran into the error Field \'actDate\' doesn\'t have a default value',1,'2016-08-16 04:53:33'),(99,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 added activity Photshop Seminar.',1,'2016-08-16 04:56:37'),(100,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 added activity Photshop Seminar.',1,'2016-08-16 04:59:21'),(101,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-16 09:46:42'),(102,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-16 12:24:37'),(103,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 ran into the error Unknown column \'assignedDate\' in \'field list\'',1,'2016-08-16 12:24:45'),(104,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-16 12:25:49'),(105,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-16 12:29:13'),(106,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-16 12:34:09'),(107,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 edited session 2: Term 3 AY 2015-2016.',1,'2016-08-16 12:36:06'),(108,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-16 12:37:41'),(109,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 had an invalid token on Get Session.',1,'2016-08-16 12:37:42'),(110,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 edited session 2: Term 3 AY 2015-2016.',1,'2016-08-16 12:37:45'),(111,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 edited session 2: Term 3 AY 2015-2016.',1,'2016-08-16 12:38:32'),(112,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-16 12:50:55'),(113,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-16 12:53:25'),(114,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 had an invalid token on Get Session.',1,'2016-08-16 12:53:26'),(115,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 edited session 2: Term 3 AY 2015-2016.',1,'2016-08-16 12:53:33'),(116,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-16 12:54:58'),(117,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 edited session 2: Term 3 AY 2015-2016.',1,'2016-08-16 12:55:25'),(118,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-16 12:58:43'),(119,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 edited session 2: Term 3 AY 2015-2016.',1,'2016-08-16 13:08:49'),(120,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 edited session 2: Term 3 AY 2015-2016.',1,'2016-08-16 13:10:27'),(121,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 edited session 2: Term 3 AY 2015-2016.',1,'2016-08-16 13:10:41'),(122,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 edited session 2: Term 3 AY 2015-2016.',1,'2016-08-16 13:11:06'),(123,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-16 13:16:18'),(124,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-16 17:01:19'),(125,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 set their session to session 1.',1,'2016-08-16 17:22:28'),(126,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 set their session to session 2.',1,'2016-08-16 17:22:31'),(127,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-17 01:20:11'),(128,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 set their session to session 2.',1,'2016-08-17 01:20:11'),(129,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-17 01:20:12'),(130,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 added activity CCS Zumba.',1,'2016-08-17 01:21:12'),(131,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-17 01:27:15'),(132,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-17 01:28:14'),(133,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 edited session 2: Term 3 AY 2015-2016.',1,'2016-08-17 01:29:19'),(134,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 edited session 2: Term 3 AY 2015-2016.',1,'2016-08-17 01:30:05'),(135,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-17 04:15:56'),(136,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-17 05:23:36'),(137,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-18 02:17:31'),(138,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-18 07:41:05'),(139,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-19 06:22:56'),(140,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-20 01:59:20'),(141,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-20 01:59:48'),(142,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 updated target group 1.',1,'2016-08-20 02:04:56'),(143,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 updated target group 1.',1,'2016-08-20 02:05:05'),(144,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 added target group Fuckboys.',1,'2016-08-20 02:05:12'),(145,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 added target group Fuckgirls.',1,'2016-08-20 02:06:56'),(146,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-20 02:12:43'),(147,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-20 02:15:14'),(148,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 deleted target group 20.',1,'2016-08-20 02:16:38'),(149,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-20 02:23:00'),(150,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 failed to update target group 21.',1,'2016-08-20 02:23:10'),(151,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 deleted target group 21.',1,'2016-08-20 02:23:15'),(152,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-20 02:33:21'),(153,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 ran into the error Missing token',1,'2016-08-20 02:33:27'),(154,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 added venue Nanat Mo.',1,'2016-08-20 02:33:54'),(155,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 added venue Ina Mo.',1,'2016-08-20 02:35:08'),(156,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 failed to update venue 6.',1,'2016-08-20 02:37:02'),(157,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 failed to update venue 6.',1,'2016-08-20 02:37:05'),(158,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-20 02:38:32'),(159,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 ran into the error Missing token',1,'2016-08-20 02:38:40'),(160,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 updated venue 6.',1,'2016-08-20 02:39:03'),(161,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 refreshed their session.',1,'2016-08-20 02:41:31'),(162,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 deleted venue 6.',1,'2016-08-20 02:41:35'),(163,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 deleted venue 7.',1,'2016-08-20 02:41:38'),(164,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 added venue G366.',1,'2016-08-20 02:41:45'),(165,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 deleted venue 8.',1,'2016-08-20 02:42:17'),(166,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 ran into the error Duplicate entry \'G366\' for key \'name\'',1,'2016-08-20 02:42:21'),(167,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 ran into the error Duplicate entry \'G366\' for key \'name\'',1,'2016-08-20 02:42:24'),(168,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 added venue G366.',1,'2016-08-20 02:43:07'),(169,'User with id#1 - ryanaustinf and ip address 0:0:0:0:0:0:0:1 updated venue 11.',1,'2016-08-20 02:43:13');
/*!40000 ALTER TABLE `gs_audit` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gs_blackdate`
--

DROP TABLE IF EXISTS `gs_blackdate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gs_blackdate` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sessionID` int(11) NOT NULL,
  `blackdate` date NOT NULL,
  `dateAdded` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `blackdatefk_1` (`sessionID`),
  CONSTRAINT `blackdatefk_1` FOREIGN KEY (`sessionID`) REFERENCES `gs_session` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gs_blackdate`
--

LOCK TABLES `gs_blackdate` WRITE;
/*!40000 ALTER TABLE `gs_blackdate` DISABLE KEYS */;
INSERT INTO `gs_blackdate` VALUES (1,1,'2016-07-06','2016-07-31 15:58:54',1),(2,2,'2016-08-15','2016-08-15 13:05:23',0),(3,2,'2016-08-13','2016-08-15 13:05:23',0),(4,2,'2016-08-13','2016-08-15 13:09:19',0),(5,2,'2016-08-11','2016-08-15 13:11:01',1),(6,2,'2016-08-10','2016-08-15 13:15:53',1);
/*!40000 ALTER TABLE `gs_blackdate` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gs_blacktime`
--

DROP TABLE IF EXISTS `gs_blacktime`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gs_blacktime` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `sessionID` int(11) NOT NULL,
  `starttime` time NOT NULL,
  `endtime` time NOT NULL,
  `dateAdded` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `blacktimefk_1` (`sessionID`),
  CONSTRAINT `blacktimefk_1` FOREIGN KEY (`sessionID`) REFERENCES `gs_session` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gs_blacktime`
--

LOCK TABLES `gs_blacktime` WRITE;
/*!40000 ALTER TABLE `gs_blacktime` DISABLE KEYS */;
INSERT INTO `gs_blacktime` VALUES (1,1,'00:00:00','07:29:00','2016-07-31 15:37:35',1),(2,1,'22:00:00','23:59:00','2016-07-31 15:37:35',1),(3,2,'00:00:00','07:30:00','2016-08-12 14:06:39',0),(4,2,'21:00:00','23:59:00','2016-08-12 14:06:39',0),(5,2,'22:00:00','23:59:00','2016-08-15 13:04:37',1),(6,2,'00:00:00','09:15:00','2016-08-15 13:09:19',0),(7,2,'00:00:00','07:30:00','2016-08-15 13:11:01',0),(8,2,'00:00:00','09:15:00','2016-08-15 13:15:53',1);
/*!40000 ALTER TABLE `gs_blacktime` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gs_session`
--

DROP TABLE IF EXISTS `gs_session`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gs_session` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userID` int(11) NOT NULL,
  `name` varchar(45) DEFAULT NULL,
  `blackdays` char(13) NOT NULL,
  `startDate` date DEFAULT NULL,
  `endDate` date DEFAULT NULL,
  `dateAdded` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `sessionfk_1` (`userID`),
  CONSTRAINT `sessionfk_1` FOREIGN KEY (`userID`) REFERENCES `gs_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gs_session`
--

LOCK TABLES `gs_session` WRITE;
/*!40000 ALTER TABLE `gs_session` DISABLE KEYS */;
INSERT INTO `gs_session` VALUES (1,1,'Test Session','1,0,0,0,0,0,0','2016-05-23','2016-08-26','2016-07-31 15:37:35',1),(2,1,'Term 3 AY 2015-2016','1,0,0,0,0,0,1','2016-05-23','2016-08-29','2016-08-12 14:06:39',1);
/*!40000 ALTER TABLE `gs_session` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gs_target_group`
--

DROP TABLE IF EXISTS `gs_target_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gs_target_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) NOT NULL,
  `name` varchar(60) NOT NULL,
  `dateAdded` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `targetfk_1` (`userId`),
  CONSTRAINT `targetfk_1` FOREIGN KEY (`userId`) REFERENCES `gs_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gs_target_group`
--

LOCK TABLES `gs_target_group` WRITE;
/*!40000 ALTER TABLE `gs_target_group` DISABLE KEYS */;
INSERT INTO `gs_target_group` VALUES (1,1,'1st Year ST','2016-08-05 05:58:59',1),(2,1,'1st Year NE','2016-08-05 06:00:13',1),(3,1,'1st Year CSE','2016-08-05 06:00:25',1),(4,1,'1st Year IT','2016-08-05 06:01:13',1),(5,1,'2nd Year ST','2016-08-05 06:01:22',1),(6,1,'2nd Year NE','2016-08-05 06:01:32',1),(7,1,'2nd Year CSE','2016-08-05 06:04:16',1),(8,1,'2nd Year IT','2016-08-05 06:04:24',1),(9,1,'2nd Year IST','2016-08-05 06:04:41',1),(10,1,'3rd Year ST','2016-08-05 06:04:49',1),(11,1,'3rd Year NE','2016-08-05 06:04:54',1),(12,1,'3rd Year CSE','2016-08-05 06:05:00',1),(13,1,'3rd Year IT','2016-08-05 06:05:04',1),(14,1,'3rd Year IST','2016-08-05 06:05:09',1),(15,1,'4th Year ST','2016-08-05 06:05:17',1),(16,1,'4th Year NE','2016-08-05 06:05:22',1),(17,1,'4th Year CSE','2016-08-05 06:05:30',1),(18,1,'4th Year IT','2016-08-05 06:05:41',1),(19,1,'4th Year IST','2016-08-05 06:05:47',1),(20,1,'Fuckboys','2016-08-20 02:05:12',0),(21,1,'Fuckgirls','2016-08-20 02:06:56',0);
/*!40000 ALTER TABLE `gs_target_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gs_user`
--

DROP TABLE IF EXISTS `gs_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gs_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL,
  `password` char(60) NOT NULL,
  `fName` varchar(45) NOT NULL,
  `lName` varchar(45) NOT NULL,
  `MI` char(3) DEFAULT NULL,
  `email` varchar(45) NOT NULL,
  `dateAdded` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gs_user`
--

LOCK TABLES `gs_user` WRITE;
/*!40000 ALTER TABLE `gs_user` DISABLE KEYS */;
INSERT INTO `gs_user` VALUES (1,'ryanaustinf','$2a$12$0DVf/uavuEssJJ8Ci0bESOVGRQX8LWhqrtSKLTNdiNQ.hdlCpyX5G','Ryan Austin','Fernandez',NULL,'ryan_fernandez@dlsu.edu.ph','2016-07-31 15:36:56',1),(2,'clareese','$2a$12$QeBMeeUqc78hHsmZlPug2O4Uu9eOQgrbvrSa7SWKC1LNmSp8xPbUe','Clarisse Felicia','Poblete','M.','clarisse_poblete@dlsu.edu.ph','2016-08-05 05:11:29',1);
/*!40000 ALTER TABLE `gs_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gs_venue`
--

DROP TABLE IF EXISTS `gs_venue`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gs_venue` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `userId` int(11) NOT NULL,
  `name` varchar(60) NOT NULL,
  `dateAdded` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `venuefk_1_idx` (`userId`),
  CONSTRAINT `venuefk_1` FOREIGN KEY (`userId`) REFERENCES `gs_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gs_venue`
--

LOCK TABLES `gs_venue` WRITE;
/*!40000 ALTER TABLE `gs_venue` DISABLE KEYS */;
INSERT INTO `gs_venue` VALUES (1,1,'ISR','2016-08-05 06:14:21',1),(2,1,'L315','2016-08-05 06:14:32',1),(3,1,'Br Rafael Donato Study Hall','2016-08-05 06:14:45',1),(4,1,'Henry Sy Sr. Hall Lobby','2016-08-05 06:16:54',1),(5,1,'G306','2016-08-16 04:47:40',1),(6,1,'Nanay Mo','2016-08-20 02:33:54',0),(7,1,'Ina Mo','2016-08-20 02:35:07',0),(8,1,'G366','2016-08-20 02:41:44',0),(11,1,'G309','2016-08-20 02:43:07',1);
/*!40000 ALTER TABLE `gs_venue` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-08-20 10:43:49
