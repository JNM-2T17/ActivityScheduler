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
  `dateAdded` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `activityfk_1` (`sessionID`),
  KEY `activityfk_2` (`venueID`),
  CONSTRAINT `activityfk_1` FOREIGN KEY (`sessionID`) REFERENCES `gs_session` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `activityfk_2` FOREIGN KEY (`venueID`) REFERENCES `gs_venue` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gs_activity`
--

LOCK TABLES `gs_activity` WRITE;
/*!40000 ALTER TABLE `gs_activity` DISABLE KEYS */;
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
  `actDate` date NOT NULL,
  `dateAdded` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `actDate` (`actDate`),
  KEY `actdatefk_1` (`actID`),
  CONSTRAINT `actdatefk_1` FOREIGN KEY (`actID`) REFERENCES `gs_activity` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gs_activity_date`
--

LOCK TABLES `gs_activity_date` WRITE;
/*!40000 ALTER TABLE `gs_activity_date` DISABLE KEYS */;
/*!40000 ALTER TABLE `gs_activity_date` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `gs_activity_target_group`
--

DROP TABLE IF EXISTS `gs_activity_target_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `gs_activity_target_group` (
  `groupID` int(11) NOT NULL,
  `actID` int(11) NOT NULL,
  `actDate` date NOT NULL,
  `dateAdded` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`groupID`,`actID`),
  UNIQUE KEY `actDate` (`actDate`),
  KEY `actgroupfk_2` (`actID`),
  CONSTRAINT `actgroupfk_1` FOREIGN KEY (`groupID`) REFERENCES `gs_target_group` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `actgroupfk_2` FOREIGN KEY (`actID`) REFERENCES `gs_activity` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gs_activity_target_group`
--

LOCK TABLES `gs_activity_target_group` WRITE;
/*!40000 ALTER TABLE `gs_activity_target_group` DISABLE KEYS */;
/*!40000 ALTER TABLE `gs_activity_target_group` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gs_blackdate`
--

LOCK TABLES `gs_blackdate` WRITE;
/*!40000 ALTER TABLE `gs_blackdate` DISABLE KEYS */;
INSERT INTO `gs_blackdate` VALUES (1,1,'2016-07-06','2016-07-31 15:58:54',1);
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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gs_blacktime`
--

LOCK TABLES `gs_blacktime` WRITE;
/*!40000 ALTER TABLE `gs_blacktime` DISABLE KEYS */;
INSERT INTO `gs_blacktime` VALUES (1,1,'00:00:00','07:29:00','2016-07-31 15:37:35',1),(2,1,'22:00:00','23:59:00','2016-07-31 15:37:35',1);
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gs_session`
--

LOCK TABLES `gs_session` WRITE;
/*!40000 ALTER TABLE `gs_session` DISABLE KEYS */;
INSERT INTO `gs_session` VALUES (1,1,'Test Session','1,0,0,0,0,0,0','2016-05-23','2016-08-31','2016-07-31 15:37:35',1);
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
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gs_target_group`
--

LOCK TABLES `gs_target_group` WRITE;
/*!40000 ALTER TABLE `gs_target_group` DISABLE KEYS */;
INSERT INTO `gs_target_group` VALUES (1,1,'1st Year ST','2016-08-05 05:58:59',1),(2,1,'1st Year NE','2016-08-05 06:00:13',1),(3,1,'1st Year CSE','2016-08-05 06:00:25',1),(4,1,'1st Year IT','2016-08-05 06:01:13',1),(5,1,'2nd Year ST','2016-08-05 06:01:22',1),(6,1,'2nd Year NE','2016-08-05 06:01:32',1),(7,1,'2nd Year CSE','2016-08-05 06:04:16',1),(8,1,'2nd Year IT','2016-08-05 06:04:24',1),(9,1,'2nd Year IST','2016-08-05 06:04:41',1),(10,1,'3rd Year ST','2016-08-05 06:04:49',1),(11,1,'3rd Year NE','2016-08-05 06:04:54',1),(12,1,'3rd Year CSE','2016-08-05 06:05:00',1),(13,1,'3rd Year IT','2016-08-05 06:05:04',1),(14,1,'3rd Year IST','2016-08-05 06:05:09',1),(15,1,'4th Year ST','2016-08-05 06:05:17',1),(16,1,'4th Year NE','2016-08-05 06:05:22',1),(17,1,'4th Year CSE','2016-08-05 06:05:30',1),(18,1,'4th Year IT','2016-08-05 06:05:41',1),(19,1,'4th Year IST','2016-08-05 06:05:47',1);
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
  UNIQUE KEY `name` (`name`),
  KEY `venuefk_1_idx` (`userId`),
  CONSTRAINT `venuefk_1` FOREIGN KEY (`userId`) REFERENCES `gs_user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `gs_venue`
--

LOCK TABLES `gs_venue` WRITE;
/*!40000 ALTER TABLE `gs_venue` DISABLE KEYS */;
INSERT INTO `gs_venue` VALUES (1,1,'ISR','2016-08-05 06:14:21',1),(2,1,'L315','2016-08-05 06:14:32',1),(3,1,'Br Rafael Donato Study Hall','2016-08-05 06:14:45',1),(4,1,'Henry Sy Sr. Hall Lobby','2016-08-05 06:16:54',1);
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

-- Dump completed on 2016-08-05 14:18:18
