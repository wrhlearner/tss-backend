-- MySQL dump 10.13  Distrib 5.7.21, for macos10.13 (x86_64)
--
-- Host: localhost    Database: tss
-- ------------------------------------------------------
-- Server version	5.7.21

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
-- Dumping data for table `major_class`
--

LOCK TABLES `major_class` WRITE;
/*!40000 ALTER TABLE `major_class` DISABLE KEYS */;
INSERT INTO `major_class` VALUES (1,'计算机科学与技术1501',2015,1),(2,'信息与电子工程学1501',2015,2),(3,'光电信息工程学1501',2015,3),(4,'控制科学和工程学1501',2015,4),(5,'控制科学和工程学1502',2015,4),(6,'土木工程学1501',2015,5),(7,'建筑学1501',2015,6),(8,'建筑学1502',2015,6),(9,'区域与城市规划1501',2015,7),(10,'区域与城市规划1502',2015,7),(11,'农业工程1501',2015,8),(12,'农业工程1502',2015,8),(13,'食品科学与营养1501',2015,9),(14,'食品科学与营养1502',2015,9),(15,'环境科学1501',2015,10),(16,'环境工程1501',2015,11),(17,'海洋科学与工程1501',2015,12),(18,'海洋科学与工程1502',2015,12),(19,'资源科学1501',2015,13),(20,'土地管理1501',2015,14),(21,'生物医学工程学1501',2015,15),(22,'生物医学工程学1502',2015,15),(23,'仪器科学与工程学1501',2015,16),(24,'农学1501',2015,17),(25,'植物保护1501',2015,18),(26,'园艺1501',2015,19),(27,'园艺1502',2015,19),(28,'茶学1501',2015,20),(29,'茶学1502',2015,20),(30,'特种经济动物科学1501',2015,21),(31,'动物科学1501',2015,22),(32,'动物医学1501',2015,23),(33,'动物医学1502',2015,23),(34,'临床医学1501',2015,24),(35,'基础医学1501',2015,25),(36,'基础医学1502',2015,25),(37,'公共卫生1501',2015,26),(38,'公共卫生1502',2015,26),(39,'护理学1501',2015,27),(40,'护理学1502',2015,27),(41,'口腔医学1501',2015,28),(42,'药学1501',2015,29),(43,'中药科学与工程1501',2015,30),(44,'管理科学与工程学1501',2015,31),(45,'工商管理学1501',2015,32),(46,'旅游管理学1501',2015,33),(47,'旅游管理学1502',2015,33),(48,'农业经济与管理学1501',2015,34),(49,'经济学1501',2015,35),(50,'经济学1502',2015,35),(51,'金融学1501',2015,36),(52,'金融学1502',2015,36),(53,'国际经济学1501',2015,37),(54,'财政学1501',2015,38),(55,'财政学1502',2015,38),(56,'公共管理学1501',2015,39),(57,'法律1501',2015,40),(58,'政治学与行政管理1501',2015,41),(59,'思想政治教育1501',2015,42),(60,'教育学1501',2015,43),(61,'教育学1502',2015,43),(62,'体育学1501',2015,44),(63,'中国语言文学1501',2015,45),(64,'历史1501',2015,46),(65,'哲学1501',2015,47),(66,'社会学1501',2015,48),(67,'社会学1502',2015,48),(68,'新闻与传播学1501',2015,49),(69,'新闻与传播学1502',2015,49),(70,'国际文化学1501',2015,50),(71,'国际文化学1502',2015,50),(72,'艺术学1501',2015,51),(73,'外国语言学与应用语言学1501',2015,52),(74,'外国语言学与应用语言学1502',2015,52),(75,'英语语言文学1501',2015,53),(76,'英语语言文学1502',2015,53),(77,'亚欧语言文学1501',2015,54),(78,'数学1501',2015,55),(79,'物理1501',2015,56),(80,'物理1502',2015,56),(81,'化学1501',2015,57),(82,'心理与行为科学1501',2015,58),(83,'心理与行为科学1502',2015,58),(84,'地球科学1501',2015,59),(85,'地球科学1502',2015,59),(86,'生物科学1501',2015,60),(87,'生物科学1502',2015,60),(88,'生物技术1501',2015,61),(89,'机械工程学1501',2015,62),(90,'机械工程学1502',2015,62),(91,'能源工程学1501',2015,63),(92,'能源工程学1502',2015,63),(93,'力学1501',2015,64),(94,'化学工程与生物工程学1501',2015,65),(95,'化学工程与生物工程学1502',2015,65),(96,'材料科学与工程学1501',2015,66),(97,'高分子科学与工程学1501',2015,67),(98,'高分子科学与工程学1502',2015,67),(99,'电机工程学1501',2015,68),(100,'电机工程学1502',2015,68),(101,'系统科学与工程学1501',2015,69),(102,'应用电子学1501',2015,70);
/*!40000 ALTER TABLE `major_class` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-05-26  0:00:50
