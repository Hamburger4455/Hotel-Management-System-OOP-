-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: hotel
-- ------------------------------------------------------
-- Server version	8.0.44

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
-- Table structure for table `guest`
--

DROP TABLE IF EXISTS `guest`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `guest` (
  `guest_id` int NOT NULL AUTO_INCREMENT,
  `full_name` varchar(100) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`guest_id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `guest`
--

LOCK TABLES `guest` WRITE;
/*!40000 ALTER TABLE `guest` DISABLE KEYS */;
INSERT INTO `guest` VALUES (1,'John Doe','555-0101'),(2,'Alice Thymefield','555-0102'),(3,'David Copperfield','555-0103'),(4,'Maria Lopez','555-0104'),(5,'William Baker','555-0105'),(6,'Sophia Martinez','555-0106'),(7,'Liam Carpenter','555-0107'),(8,'Olivia Rodrigo','555-0108'),(9,'Benjamin White','555-0109'),(10,'Emma Davis','555-0110'),(12,'Bo Pc','123123'),(13,'Bo Cd','124124'),(14,'Jane Doe','11010101');
/*!40000 ALTER TABLE `guest` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `invoice`
--

DROP TABLE IF EXISTS `invoice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `invoice` (
  `invoice_id` int NOT NULL AUTO_INCREMENT,
  `reservation_id` int NOT NULL,
  `subtotal` decimal(10,2) NOT NULL DEFAULT '0.00',
  `tax` decimal(10,2) NOT NULL DEFAULT '0.00',
  `total` decimal(10,2) NOT NULL DEFAULT '0.00',
  `is_paid` tinyint NOT NULL DEFAULT '0',
  PRIMARY KEY (`invoice_id`),
  UNIQUE KEY `uq_inv_reservation` (`reservation_id`),
  CONSTRAINT `fk_inv_reservation` FOREIGN KEY (`reservation_id`) REFERENCES `reservation` (`reservation_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invoice`
--

LOCK TABLES `invoice` WRITE;
/*!40000 ALTER TABLE `invoice` DISABLE KEYS */;
INSERT INTO `invoice` VALUES (1,1,220.00,22.00,242.00,1),(2,2,260.00,26.00,286.00,1),(3,3,800.00,80.00,880.00,1),(4,4,0.00,0.00,0.00,1),(5,5,180.00,18.00,198.00,1),(6,6,600.00,60.00,660.00,1),(7,7,420.00,42.00,462.00,1),(8,8,350.00,35.00,385.00,1),(9,9,160.00,16.00,176.00,0),(10,10,750.00,75.00,825.00,0),(11,11,360.00,36.00,396.00,1),(12,12,90.00,9.00,99.00,0);
/*!40000 ALTER TABLE `invoice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment`
--

DROP TABLE IF EXISTS `payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment` (
  `payment_id` int NOT NULL AUTO_INCREMENT,
  `invoice_id` int NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `method` enum('CASH','CARD') NOT NULL,
  `status` enum('SUCCESS','FAILED','PENDING') NOT NULL DEFAULT 'PENDING',
  `payment_date` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`payment_id`),
  KEY `fk_payment_invoice` (`invoice_id`),
  CONSTRAINT `fk_payment_invoice` FOREIGN KEY (`invoice_id`) REFERENCES `invoice` (`invoice_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment`
--

LOCK TABLES `payment` WRITE;
/*!40000 ALTER TABLE `payment` DISABLE KEYS */;
INSERT INTO `payment` VALUES (1,1,242.00,'CARD','SUCCESS','2025-10-03 10:15:00'),(2,3,880.00,'CASH','SUCCESS','2025-10-10 09:00:00'),(3,2,100.00,'CARD','SUCCESS','2025-10-06 18:30:00'),(4,5,100.00,'CARD','SUCCESS','2025-10-18 09:00:00'),(5,5,98.00,'CARD','SUCCESS','2025-10-18 09:30:00'),(6,6,200.00,'CARD','SUCCESS','2025-10-20 11:00:00'),(7,7,462.00,'CASH','SUCCESS','2025-10-26 12:15:00'),(8,8,385.00,'CARD','SUCCESS','2025-11-04 10:20:00'),(9,9,100.00,'CARD','SUCCESS','2025-11-13 09:50:00'),(10,10,0.00,'CASH','PENDING','2025-11-18 14:00:00'),(11,11,396.00,'CASH','SUCCESS','2025-11-27 03:51:55');
/*!40000 ALTER TABLE `payment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `receptionist`
--

DROP TABLE IF EXISTS `receptionist`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `receptionist` (
  `staff_id` int NOT NULL AUTO_INCREMENT,
  `full_name` varchar(100) NOT NULL,
  `shift` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`staff_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `receptionist`
--

LOCK TABLES `receptionist` WRITE;
/*!40000 ALTER TABLE `receptionist` DISABLE KEYS */;
INSERT INTO `receptionist` VALUES (1,'Sarah Brown','Morning'),(2,'Michael Jackson','Afternoon'),(3,'Emma Wilson','Night'),(4,'Jacob Moore','Morning'),(5,'Ava Hernandez','Evening'),(6,'Noah Peterson','Night');
/*!40000 ALTER TABLE `receptionist` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reservation`
--

DROP TABLE IF EXISTS `reservation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reservation` (
  `reservation_id` int NOT NULL AUTO_INCREMENT,
  `guest_id` int NOT NULL,
  `room_id` int NOT NULL,
  `check_in_date` date NOT NULL,
  `check_out_date` date NOT NULL,
  `status` enum('PENDING','CONFIRMED','CHECKED_IN','CHECKED_OUT','CANCELLED') NOT NULL DEFAULT 'PENDING',
  PRIMARY KEY (`reservation_id`),
  KEY `fk_res_guest` (`guest_id`),
  KEY `fk_res_room` (`room_id`),
  CONSTRAINT `fk_res_guest` FOREIGN KEY (`guest_id`) REFERENCES `guest` (`guest_id`),
  CONSTRAINT `fk_res_room` FOREIGN KEY (`room_id`) REFERENCES `room` (`room_id`),
  CONSTRAINT `chk_res_dates` CHECK ((`check_out_date` > `check_in_date`))
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reservation`
--

LOCK TABLES `reservation` WRITE;
/*!40000 ALTER TABLE `reservation` DISABLE KEYS */;
INSERT INTO `reservation` VALUES (1,1,101,'2025-10-01','2025-10-03','CHECKED_OUT'),(2,2,201,'2025-10-05','2025-10-07','CHECKED_OUT'),(3,3,301,'2025-10-10','2025-10-15','CHECKED_OUT'),(4,4,401,'2025-10-10','2025-10-15','CHECKED_OUT'),(5,5,102,'2025-10-16','2025-10-18','CHECKED_OUT'),(6,6,302,'2025-10-17','2025-10-20','CHECKED_OUT'),(7,7,401,'2025-10-24','2025-10-26','CHECKED_OUT'),(8,8,201,'2025-11-01','2025-11-04','CHECKED_OUT'),(9,9,101,'2025-11-10','2025-11-13','CONFIRMED'),(10,10,302,'2025-11-14','2025-11-18','PENDING'),(11,12,401,'2025-11-27','2025-11-30','CHECKED_IN'),(12,13,201,'2025-11-28','2025-11-29','CHECKED_IN');
/*!40000 ALTER TABLE `reservation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `room`
--

DROP TABLE IF EXISTS `room`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `room` (
  `room_id` int NOT NULL AUTO_INCREMENT,
  `room_type` enum('STANDARD','DELUXE','SUITE','FAMILY') NOT NULL,
  `status` enum('AVAILABLE','OCCUPIED','MAINTENANCE') NOT NULL DEFAULT 'AVAILABLE',
  `rate_per_night` decimal(10,2) NOT NULL,
  PRIMARY KEY (`room_id`)
) ENGINE=InnoDB AUTO_INCREMENT=403 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `room`
--

LOCK TABLES `room` WRITE;
/*!40000 ALTER TABLE `room` DISABLE KEYS */;
INSERT INTO `room` VALUES (101,'STANDARD','OCCUPIED',60.00),(102,'STANDARD','AVAILABLE',60.00),(201,'DELUXE','OCCUPIED',90.00),(202,'DELUXE','AVAILABLE',95.00),(301,'SUITE','AVAILABLE',150.00),(302,'SUITE','OCCUPIED',160.00),(401,'FAMILY','OCCUPIED',120.00),(402,'FAMILY','AVAILABLE',130.00);
/*!40000 ALTER TABLE `room` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-12-01  9:00:10
