CREATE TABLE `users` (
  `userid` varchar(5) NOT NULL,
  `username` varchar(45) NOT NULL,
  `password` varchar(60) NOT NULL,
  `enabled` tinyint(4) NOT NULL DEFAULT '1',
  PRIMARY KEY (`userid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `user_roles` (
  `user_role_id` int(11) NOT NULL AUTO_INCREMENT,
  `userid` varchar(5) NOT NULL,
  `role` varchar(45) NOT NULL,
  PRIMARY KEY (`user_role_id`),
  UNIQUE KEY `uni_username_role` (`role`,`userid`),
  KEY `fk_username_idx` (`userid`),
  CONSTRAINT `fk_username` FOREIGN KEY (`userid`) REFERENCES `users` (`userid`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

CREATE TABLE `workflow_element` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(256) NOT NULL,
  `parent_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `parent_key_idx` (`parent_id`),
  CONSTRAINT `parent_key` FOREIGN KEY (`parent_id`) REFERENCES `workflow_element` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `acespm`.`workflow_element` 
ADD INDEX `parent_key_idx` (`parent_id` ASC);
ALTER TABLE `acespm`.`workflow_element` 
ADD CONSTRAINT `parent_key`
  FOREIGN KEY (`parent_id`)
  REFERENCES `acespm`.`workflow_element` (`id`)
  ON DELETE CASCADE
  ON UPDATE NO ACTION;

CREATE TABLE `response_set` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `resp_type` varchar(4) NOT NULL,
  `options` TEXT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `response_link` (
  `elem_id` int(11) NOT NULL,
  `resp_id` int(11) NOT NULL,
  PRIMARY KEY (`elem_id`,`resp_id`),
  KEY `fk_resp_idx` (`resp_id`),
  CONSTRAINT `fk_elem` FOREIGN KEY (`elem_id`) REFERENCES `workflow_element` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `fk_resp` FOREIGN KEY (`resp_id`) REFERENCES `response_set` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `acespm`.`response_set` 
ADD COLUMN `title` NVARCHAR(512) NULL DEFAULT NULL AFTER `options`;
