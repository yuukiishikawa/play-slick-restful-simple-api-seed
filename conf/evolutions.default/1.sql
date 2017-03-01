Drop table ApiLog;

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ApiLog` (
  `datetime` timestamp NOT NULL,
  `ip`  varchar(255) NOT NULL,
  `token`  varchar(255) DEFAULT NULL,
  `method`  varchar(255) NOT NULL,
  `uri`  varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `requestBody`  text DEFAULT NULL,
  `responseStatus` int(11) NOT NULL,
  `responseBody`  text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE INDEX idx_log_datetime ON ApiLog(datetime);
CREATE INDEX idx_log_token ON ApiLog(token);
CREATE INDEX idx_log_email ON ApiLog(email);

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `AuthToken` (
  `authKey` varchar(40) NOT NULL,
  `email` varchar(255) NOT NULL,
  `expiresAt` datetime NOT NULL,
  `updatedAt` datetime NOT NULL,
  PRIMARY KEY (`authKey`),
  UNIQUE KEY `authKey_UNIQUE` (`authKey`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

