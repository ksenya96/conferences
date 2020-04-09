CREATE DATABASE confs CHARACTER SET utf8 COLLATE utf8_general_ci;
USE confs;

CREATE TABLE `event` (
	`event_id` INT(10) NOT NULL AUTO_INCREMENT,
	`name` varchar(255) NOT NULL,
	`short_description` varchar(600) NOT NULL,
	`full_description` TEXT NOT NULL,
	`start_date` DATETIME NOT NULL,
	`end_date` DATETIME NOT NULL,
	`main_image` BINARY NOT NULL,
	PRIMARY KEY (`event_id`)
);

CREATE TABLE `area` (
	`area_id` INT(10) NOT NULL AUTO_INCREMENT,
	`name` varchar(255) NOT NULL UNIQUE,
	PRIMARY KEY (`area_id`)
);

CREATE TABLE `area_event_ref` (
	`area_id` INT(10) NOT NULL,
	`event_id` INT(10) NOT NULL
);

CREATE TABLE `keyword` (
	`keyword_id` INT(10) NOT NULL AUTO_INCREMENT,
	`name` varchar(255) NOT NULL UNIQUE,
	PRIMARY KEY (`keyword_id`)
);

CREATE TABLE `image` (
	`image_id` INT(10) NOT NULL AUTO_INCREMENT,
	`content` BINARY NOT NULL,
	`event_id` INT(10) NOT NULL,
	PRIMARY KEY (`image_id`)
);

CREATE TABLE `organization` (
	`organization_id` INT(10) NOT NULL AUTO_INCREMENT,
	`name` varchar(255) NOT NULL UNIQUE,
	`user_id` INT(10) NOT NULL,
	PRIMARY KEY (`organization_id`)
);

CREATE TABLE `organization_event_ref` (
	`organization_id` INT(10) NOT NULL,
	`event_id` INT(10) NOT NULL
);

CREATE TABLE `country` (
	`country_id` INT(10) NOT NULL AUTO_INCREMENT,
	`name` varchar(255) NOT NULL UNIQUE,
	PRIMARY KEY (`country_id`)
);

CREATE TABLE `city` (
	`city_id` INT(10) NOT NULL AUTO_INCREMENT,
	`name` varchar(255) NOT NULL,
	`country_id` INT(10) NOT NULL,
	PRIMARY KEY (`city_id`)
);

CREATE TABLE `location` (
	`location_id` INT(10) NOT NULL AUTO_INCREMENT,
	`address` varchar(1000) NOT NULL,
	`province` varchar(255),
	`postal_code` varchar(100) NOT NULL,
	`geolocation` varchar(100) NOT NULL,
	`city_id` INT(10) NOT NULL,
	`event_id` INT(10) NOT NULL,
	`status` varchar(255) NOT NULL,
	PRIMARY KEY (`location_id`)
);

CREATE TABLE `location_status` (
	`name` varchar(255) NOT NULL UNIQUE,
	PRIMARY KEY (`name`)
);

CREATE TABLE `keyword_event_ref` (
	`keyword_id` INT(10) NOT NULL,
	`event_id` INT(10) NOT NULL
);

CREATE TABLE `link` (
	`link_id` INT(10) NOT NULL AUTO_INCREMENT,
	`value` varchar(1000) NOT NULL,
	`type` varchar(255) NOT NULL,
	PRIMARY KEY (`link_id`)
);

CREATE TABLE `link_type` (
	`type` varchar(255) NOT NULL,
	PRIMARY KEY (`type`)
);

CREATE TABLE `language` (
	`name` varchar(255) NOT NULL,
	PRIMARY KEY (`name`)
);

CREATE TABLE `language_event_ref` (
	`language` varchar(255) NOT NULL,
	`event_id` INT(10) NOT NULL
);

CREATE TABLE `sponsor` (
	`sponsor_id` INT(10) NOT NULL AUTO_INCREMENT,
	`name` varchar(255) NOT NULL,
	`logo` BINARY,
	`event_id` INT(10) NOT NULL,
	PRIMARY KEY (`sponsor_id`)
);

CREATE TABLE `speaker` (
	`speaker_id` INT(10) NOT NULL AUTO_INCREMENT,
	`name` varchar(255) NOT NULL,
	`description` TEXT,
	`photo` BINARY,
	`title` varchar(255) NOT NULL,
	`event_id` INT(10) NOT NULL,
	PRIMARY KEY (`speaker_id`)
);

CREATE TABLE `user` (
	`user_id` INT(10) NOT NULL AUTO_INCREMENT,
	`name` varchar(255) NOT NULL,
	`email` varchar(255) NOT NULL,
	`phone` varchar(255),
	`address` varchar(1000),
	`role_name` varchar(1000),
	`password` varchar(100),
	`username` varchar(255) NOT NULL,
	`oauth2_resource` ENUM('GOOGLE', 'FACEBOOK', 'TWITTER', 'LINKEDIN', 'GITHUB'),
	PRIMARY KEY (`user_id`)
);

CREATE TABLE `role` (
	`name` varchar(100) NOT NULL,
	PRIMARY KEY (`name`)
);

CREATE TABLE `link_event_ref` (
	`link_id` INT(10) NOT NULL,
	`event_id` INT(10) NOT NULL
);

CREATE TABLE `speaker_link_ref` (
	`speaker_id` INT(10) NOT NULL,
	`link_id` INT(10) NOT NULL
);

CREATE TABLE `oauth2_user` (
	`user_id` INT(10) NOT NULL,
	`oauth2_name` varchar(255) NOT NULL,
	`oauth2_username` varchar(255) NOT NULL,
	`type` ENUM('google', 'facebook', 'twitter', 'linkedin') NOT NULL,
	PRIMARY KEY (`user_id`)
);

CREATE TABLE `invalid_token` (
	`token_id` int(10) NOT NULL AUTO_INCREMENT,
	`token` VARCHAR(1000) NOT NULL,
	`expiration_time` DATETIME NOT NULL,
	CONSTRAINT invalid_token_pk
		PRIMARY KEY (`token_id`)
);

ALTER TABLE `area_event_ref` ADD CONSTRAINT `area_event_ref_fk0` FOREIGN KEY (`area_id`) REFERENCES `area`(`area_id`);

ALTER TABLE `area_event_ref` ADD CONSTRAINT `area_event_ref_fk1` FOREIGN KEY (`event_id`) REFERENCES `event`(`event_id`);

ALTER TABLE `image` ADD CONSTRAINT `image_fk0` FOREIGN KEY (`event_id`) REFERENCES `event`(`event_id`);

ALTER TABLE `organization` ADD CONSTRAINT `organization_fk0` FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`);

ALTER TABLE `organization_event_ref` ADD CONSTRAINT `organization_event_ref_fk0` FOREIGN KEY (`organization_id`) REFERENCES `organization`(`organization_id`);

ALTER TABLE `organization_event_ref` ADD CONSTRAINT `organization_event_ref_fk1` FOREIGN KEY (`event_id`) REFERENCES `event`(`event_id`);

ALTER TABLE `city` ADD CONSTRAINT `city_fk0` FOREIGN KEY (`country_id`) REFERENCES `country`(`country_id`);

ALTER TABLE `location` ADD CONSTRAINT `location_fk0` FOREIGN KEY (`city_id`) REFERENCES `city`(`city_id`);

ALTER TABLE `location` ADD CONSTRAINT `location_fk1` FOREIGN KEY (`event_id`) REFERENCES `event`(`event_id`);

ALTER TABLE `location` ADD CONSTRAINT `location_fk2` FOREIGN KEY (`status`) REFERENCES `location_status`(`name`);

ALTER TABLE `keyword_event_ref` ADD CONSTRAINT `keyword_event_ref_fk0` FOREIGN KEY (`keyword_id`) REFERENCES `keyword`(`keyword_id`);

ALTER TABLE `keyword_event_ref` ADD CONSTRAINT `keyword_event_ref_fk1` FOREIGN KEY (`event_id`) REFERENCES `event`(`event_id`);

ALTER TABLE `link` ADD CONSTRAINT `link_fk0` FOREIGN KEY (`type`) REFERENCES `link_type`(`type`);

ALTER TABLE `language_event_ref` ADD CONSTRAINT `language_event_ref_fk0` FOREIGN KEY (`language`) REFERENCES `language`(`name`);

ALTER TABLE `language_event_ref` ADD CONSTRAINT `language_event_ref_fk1` FOREIGN KEY (`event_id`) REFERENCES `event`(`event_id`);

ALTER TABLE `sponsor` ADD CONSTRAINT `sponsor_fk0` FOREIGN KEY (`event_id`) REFERENCES `event`(`event_id`);

ALTER TABLE `speaker` ADD CONSTRAINT `speaker_fk0` FOREIGN KEY (`event_id`) REFERENCES `event`(`event_id`);

ALTER TABLE `user` ADD CONSTRAINT `user_fk0` FOREIGN KEY (`role_name`) REFERENCES `role`(`name`);

ALTER TABLE `link_event_ref` ADD CONSTRAINT `link_event_ref_fk0` FOREIGN KEY (`link_id`) REFERENCES `link`(`link_id`);

ALTER TABLE `link_event_ref` ADD CONSTRAINT `link_event_ref_fk1` FOREIGN KEY (`event_id`) REFERENCES `event`(`event_id`);

ALTER TABLE `speaker_link_ref` ADD CONSTRAINT `speaker_link_ref_fk0` FOREIGN KEY (`speaker_id`) REFERENCES `speaker`(`speaker_id`);

ALTER TABLE `speaker_link_ref` ADD CONSTRAINT `speaker_link_ref_fk1` FOREIGN KEY (`link_id`) REFERENCES `link`(`link_id`);

ALTER TABLE `oauth2_user` ADD CONSTRAINT `oauth2_user_fk0` FOREIGN KEY (`user_id`) REFERENCES `user`(`user_id`);

