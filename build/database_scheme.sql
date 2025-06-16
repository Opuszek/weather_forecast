CREATE DATABASE IF NOT EXISTS weather_observer;
CREATE USER IF NOT EXISTS 'TTH'@'localhost' IDENTIFIED BY 'asd123';
GRANT ALL PRIVILEGES ON weather_observer.* To 'TTH'@'localhost';
USE weather_observer;

CREATE TABLE IF NOT EXISTS `city` (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` char(255) DEFAULT NULL,
  `country` char(255) DEFAULT NULL,
  `longitude` float DEFAULT NULL,
  `latitude` float DEFAULT NULL,
  `error` char(255) DEFAULT NULL,
  `number_of_tries` int DEFAULT '0',
  `invalid` tinyint(1) DEFAULT '0',
  `located` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `city` (name, country) VALUES ('Krakow','Poland');

CREATE TABLE IF NOT EXISTS `forecast_error` (
  `city_id` int NOT NULL,
  `date` datetime NOT NULL,
  `error` varchar(255) DEFAULT NULL,
  KEY `city_id` (`city_id`),
  CONSTRAINT `forecast_error_ibfk_1` FOREIGN KEY (`city_id`) REFERENCES `city` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `temperature_forecast` (
  `id` int NOT NULL AUTO_INCREMENT,
  `city_id` int DEFAULT NULL,
  `max_temperature` float DEFAULT NULL,
  `min_temperature` float DEFAULT NULL,
  `rain_sum` float DEFAULT NULL,
  `day` date DEFAULT NULL,
  `sunrise` timestamp NULL DEFAULT NULL,
  `sunset` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_city_forecast` (`city_id`),
  CONSTRAINT `fk_city_forecast` FOREIGN KEY (`city_id`) REFERENCES `city` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
