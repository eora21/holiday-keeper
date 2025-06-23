DROP TABLE IF EXISTS `holiday_types`;
DROP TABLE IF EXISTS `type`;
DROP TABLE IF EXISTS `holiday_counties`;
DROP TABLE IF EXISTS `counties`;
DROP TABLE IF EXISTS `holidays`;
DROP TABLE IF EXISTS `countries`;

CREATE TABLE `countries`
(
    `country_code` VARCHAR(2) PRIMARY KEY,
    `name`         VARCHAR(100) NOT NULL
);

CREATE TABLE `holidays`
(
    `holiday_id`   BIGINT AUTO_INCREMENT PRIMARY KEY,
    `date`         DATE         NOT NULL,
    `name`         VARCHAR(100) NOT NULL,
    `local_name`   VARCHAR(100) NOT NULL,
    `country_code` VARCHAR(2)   NOT NULL,

    FOREIGN KEY (`country_code`) REFERENCES countries (`country_code`),

    INDEX `idx_holidays_date` (`date`)
);

CREATE TABLE `counties`
(
    `county_code` VARCHAR(6) PRIMARY KEY
);

CREATE TABLE `holiday_counties`
(
    `holiday_id`  BIGINT,
    `county_code` VARCHAR(6),

    PRIMARY KEY (`holiday_id`, `county_code`),
    FOREIGN KEY (`holiday_id`) REFERENCES holidays (`holiday_id`),
    FOREIGN KEY (`county_code`) REFERENCES counties (`county_code`)
);

CREATE TABLE `type`
(
    `type_code` VARCHAR(20) PRIMARY KEY
);

CREATE TABLE `holiday_types`
(
    `holiday_id` BIGINT,
    `type_code`  VARCHAR(20),

    PRIMARY KEY (`holiday_id`, `type_code`),
    FOREIGN KEY (`holiday_id`) REFERENCES holidays (`holiday_id`),
    FOREIGN KEY (`type_code`) REFERENCES type (`type_code`)
);
