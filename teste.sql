-- phpMyAdmin SQL Dump
-- version 4.4.10
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Dec 19, 2015 at 08:07 PM
-- Server version: 5.5.42
-- PHP Version: 5.6.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

--
-- Database: `SD`
--

-- --------------------------------------------------------

--
-- Table structure for table `alternative`
--

CREATE TABLE `alternative` (
  `id_alternative` bigint(20) NOT NULL,
  `description` varchar(200) COLLATE utf8_bin NOT NULL,
  `n_votes` int(11) DEFAULT '0',
  `divisor` double NOT NULL DEFAULT '1',
  `id_project` bigint(20) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `alternative`
--

INSERT INTO `alternative` (`id_alternative`, `description`, `n_votes`, `divisor`, `id_project`) VALUES
(1, 'chocolate', 0, 5, 3),
(2, 'caramelo', 0, 5, 3),
(3, 'teste alternative', 1, 5, 4),
(4, 'azul', 1, 5, 6),
(5, 'amarelo', 0, 5, 6),
(6, 'teste alternative', 1, 5, 8);

--
-- Triggers `alternative`
--
DELIMITER $$
CREATE TRIGGER `check_divisor` BEFORE INSERT ON `alternative`
 FOR EACH ROW BEGIN
  IF new.divisor < 1 THEN 
      SIGNAL SQLSTATE '45000'
          SET MESSAGE_TEXT = 'Divisor must be greater or equal than 1';
    END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `donation`
--

CREATE TABLE `donation` (
  `id_donation` bigint(20) NOT NULL,
  `pledge_value` float NOT NULL,
  `id_user` bigint(20) DEFAULT NULL,
  `id_reward` bigint(20) DEFAULT NULL,
  `id_alternative` bigint(20) DEFAULT NULL,
  `id_project` bigint(11) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `donation`
--

INSERT INTO `donation` (`id_donation`, `pledge_value`, `id_user`, `id_reward`, `id_alternative`, `id_project`) VALUES
(2, 75, 3, 0, 0, 1),
(3, 50, 4, 7, 0, 5),
(4, 40, 4, 0, 4, 6),
(5, 9999, 1, 6, 3, 4),
(6, 100, 1, 11, 6, 8);

--
-- Triggers `donation`
--
DELIMITER $$
CREATE TRIGGER `check_pledge_value` BEFORE INSERT ON `donation`
 FOR EACH ROW BEGIN
  IF new.pledge_value <1 THEN 
      SIGNAL SQLSTATE '45000'
          SET MESSAGE_TEXT = 'Pledge value must be greater or equal than 1';
    END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `message`
--

CREATE TABLE `message` (
  `id_message` bigint(20) NOT NULL,
  `text` varchar(400) COLLATE utf8_bin DEFAULT NULL,
  `id_user` bigint(20) DEFAULT NULL,
  `id_project` bigint(20) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `message`
--

INSERT INTO `message` (`id_message`, `text`, `id_user`, `id_project`) VALUES
(3, 'joaoprg:ola\n    19/12/2015 18:01', 3, 3),
(4, 'joaoprg:ola\n    19/12/2015 18:02', 3, 3),
(5, 'joaoprg:ola\n    19/12/2015 18:03', 3, 3),
(6, 'joaoprg:sdafgshfj\n    19/12/2015 18:04', 3, 3),
(7, 'joaoprg:lknjklnç\n   19/12/2015 18:07', 3, 3),
(8, 'joao:comentario a passar\n   19/12/2015 18:11', 1, 4),
(9, 'joaoprg:gewherhsgmterhhrtregdngrggnrg\n    19/12/2015 18:18', 3, 3),
(10, 'joaoprg:gafdgjfkgl\n    19/12/2015 18:35', 3, 3),
(11, 'joaoprg:asdfgfs\n   19/12/2015 18:37', 3, 3),
(12, 'joaoprg:ola\n   19/12/2015 18:52', 3, 3),
(13, 'joaoprg:adsfgf\n    19/12/2015 18:52', 3, 3);

-- --------------------------------------------------------

--
-- Table structure for table `project`
--

CREATE TABLE `project` (
  `id_project` bigint(20) NOT NULL,
  `name` varchar(50) COLLATE utf8_bin NOT NULL,
  `description` varchar(400) COLLATE utf8_bin NOT NULL,
  `limit_date` varchar(30) COLLATE utf8_bin NOT NULL,
  `target_value` bigint(20) NOT NULL,
  `current_value` bigint(20) DEFAULT '0',
  `enterprise` varchar(30) COLLATE utf8_bin DEFAULT NULL,
  `id_user` bigint(20) DEFAULT NULL,
  `accepted` int(11) NOT NULL DEFAULT '0',
  `post_id` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `base_hostname` varchar(255) COLLATE utf8_bin DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `project`
--

INSERT INTO `project` (`id_project`, `name`, `description`, `limit_date`, `target_value`, `current_value`, `enterprise`, `id_user`, `accepted`, `post_id`, `base_hostname`) VALUES
(3, 'bolachas pipoca', 'cheias da fibra', '12/12/2222 16', 500, 0, '', 1, 0, NULL, NULL),
(4, 'cenas2', 'rewty', '27/12/2015 16', 1234, 9999, 'cenas lds', 3, 0, NULL, NULL),
(5, 'o tumblr e bonito', 'teste tumblr', '12/04/2016 17', 500, 50, '', 1, 0, '135516666676', 'lelo-das-barracas-love.tumblr.com'),
(6, 'testes de SD', 'kinder chocolate', '12/09/2016 18', 100, 40, '', 1, 0, '135517256156', 'lelo-das-barracas-love.tumblr.com'),
(7, 'comer macas', 'isso mesmo', '12/12/2016 02', 1000, 0, '', 1, 0, NULL, NULL),
(8, 'ola', 'opa', '12/12/2016 20', 12343, 100, 'cenas lds', 4, 0, '135519419122', 'joaoprg.tumblr.com');

--
-- Triggers `project`
--
DELIMITER $$
CREATE TRIGGER `check_target_value` BEFORE INSERT ON `project`
 FOR EACH ROW BEGIN
  IF new.target_value < 1 THEN 
      SIGNAL SQLSTATE '45000'
          SET MESSAGE_TEXT = 'Target value must be greater or equal than 1';
    END IF;
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `check_target_value2` BEFORE UPDATE ON `project`
 FOR EACH ROW BEGIN
  IF new.target_value < 1 THEN 
      SIGNAL SQLSTATE '45000'
          SET MESSAGE_TEXT = 'Target value must be greater or equal than 1';
    END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `project_has_user`
--

CREATE TABLE `project_has_user` (
  `id_project_user` bigint(20) NOT NULL,
  `id_project` bigint(20) NOT NULL,
  `id_user` bigint(20) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `project_has_user`
--

INSERT INTO `project_has_user` (`id_project_user`, `id_project`, `id_user`) VALUES
(3, 5, 4),
(4, 6, 4),
(5, 4, 1),
(6, 8, 1);

-- --------------------------------------------------------

--
-- Table structure for table `reply`
--

CREATE TABLE `reply` (
  `id_reply` bigint(20) NOT NULL,
  `text` varchar(400) COLLATE utf8_bin NOT NULL,
  `id_user` bigint(20) NOT NULL,
  `id_project` bigint(20) NOT NULL,
  `id_message` bigint(20) NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `reply`
--

INSERT INTO `reply` (`id_reply`, `text`, `id_user`, `id_project`, `id_message`) VALUES
(1, 'Admin''s reply : caralho\n   19/12/2015 19:02', 3, 4, 8);

-- --------------------------------------------------------

--
-- Table structure for table `reward`
--

CREATE TABLE `reward` (
  `id_reward` bigint(20) NOT NULL,
  `description` varchar(200) COLLATE utf8_bin NOT NULL,
  `min_value` double NOT NULL,
  `id_project` bigint(20) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `reward`
--

INSERT INTO `reward` (`id_reward`, `description`, `min_value`, `id_project`) VALUES
(5, '1 balde', 100, 3),
(6, 'um teste ', 5, 4),
(7, 'testes loucos', 10, 5),
(8, 'cenas ', 100, 6),
(9, 'maca reineta', 12, 7),
(10, 'maca royal', 34, 7),
(11, 'cenas rewards', 5, 8);

--
-- Triggers `reward`
--
DELIMITER $$
CREATE TRIGGER `check_min_value` BEFORE INSERT ON `reward`
 FOR EACH ROW BEGIN
  IF new.min_value <1 THEN 
      SIGNAL SQLSTATE '45000'
          SET MESSAGE_TEXT = 'Minimum value of pledge must be greater or equal than 1';
    END IF;
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `check_min_value2` BEFORE UPDATE ON `reward`
 FOR EACH ROW BEGIN
  IF new.min_value <1 THEN 
      SIGNAL SQLSTATE '45000'
          SET MESSAGE_TEXT = 'Minimum value of pledge must be greater or equal than 1';
    END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `id_user` bigint(20) NOT NULL,
  `name` varchar(50) COLLATE utf8_bin NOT NULL,
  `password` varchar(50) COLLATE utf8_bin DEFAULT NULL,
  `bi` varchar(25) COLLATE utf8_bin DEFAULT NULL,
  `age` int(5) DEFAULT NULL,
  `email` varchar(30) COLLATE utf8_bin DEFAULT NULL,
  `account_balance` bigint(20) DEFAULT '100',
  `is_tumblr_account` int(2) NOT NULL DEFAULT '0',
  `secret_token` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `user_token` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `tumblr_username` varchar(50) COLLATE utf8_bin DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`id_user`, `name`, `password`, `bi`, `age`, `email`, `account_balance`, `is_tumblr_account`, `secret_token`, `user_token`, `tumblr_username`) VALUES
(1, 'joao', 's', '12345', 22, 'joao@sapo.pt', 999989901, 0, 'gA0NJ374NyFw6Q1ihKkZn6VOHvsdmijAF3rVJRjsLhAiwg5ch8', 'YEzl9TDHXIYMcxc4Yn952F4gGc6yeyU5KIw70N3ojbQvIho1YY', 'lelo-das-barracas-love'),
(2, 'carlos', 's', '987', 13, 'a@b.c', 10000000000, 0, NULL, NULL, NULL),
(3, 'joaoprg', '1234', '1234567', 22, 'joao_230793@hotmail.com', 1000000000, 0, NULL, NULL, NULL),
(4, 'joaoprg', NULL, NULL, NULL, NULL, 10, 1, '0S8iqTn6mvsvin7XFEhE72NGqS8NgWOJ1oiPVS3RjF6tprJ278', 'tsM3KqgUfwBwtQEtuNgKbPRKbAkWAUKuxOuV7njK25pnA1fn3r', NULL);

--
-- Triggers `user`
--
DELIMITER $$
CREATE TRIGGER `check_balance` BEFORE UPDATE ON `user`
 FOR EACH ROW BEGIN
  IF new.account_balance <0 THEN 
      SIGNAL SQLSTATE '45000'
          SET MESSAGE_TEXT = 'Account balance must be greater or equal than 0';
    END IF;
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `check_balance2` BEFORE INSERT ON `user`
 FOR EACH ROW BEGIN
  IF new.account_balance != 100 THEN 
      SIGNAL SQLSTATE '45000'
          SET MESSAGE_TEXT = 'Initial account balance must be 100';
    END IF;
END
$$
DELIMITER ;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `alternative`
--
ALTER TABLE `alternative`
  ADD PRIMARY KEY (`id_alternative`),
  ADD KEY `fk_al_proj` (`id_project`);

--
-- Indexes for table `donation`
--
ALTER TABLE `donation`
  ADD PRIMARY KEY (`id_donation`),
  ADD KEY `fk_don_user` (`id_user`),
  ADD KEY `fk_reward` (`id_reward`),
  ADD KEY `fk_alternative` (`id_alternative`);

--
-- Indexes for table `message`
--
ALTER TABLE `message`
  ADD PRIMARY KEY (`id_message`),
  ADD KEY `fk_msg_user` (`id_user`),
  ADD KEY `fk_msg_project` (`id_project`);

--
-- Indexes for table `project`
--
ALTER TABLE `project`
  ADD PRIMARY KEY (`id_project`),
  ADD UNIQUE KEY `name` (`name`),
  ADD KEY `fk_admin` (`id_user`);

--
-- Indexes for table `project_has_user`
--
ALTER TABLE `project_has_user`
  ADD PRIMARY KEY (`id_project_user`),
  ADD KEY `fk_project_user` (`id_project`),
  ADD KEY `fk_user_project` (`id_user`);

--
-- Indexes for table `reply`
--
ALTER TABLE `reply`
  ADD PRIMARY KEY (`id_reply`),
  ADD KEY `id_user` (`id_user`),
  ADD KEY `id_project` (`id_project`),
  ADD KEY `id_message` (`id_message`);

--
-- Indexes for table `reward`
--
ALTER TABLE `reward`
  ADD PRIMARY KEY (`id_reward`),
  ADD KEY `fk_project` (`id_project`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id_user`),
  ADD UNIQUE KEY `uni_bi` (`bi`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `alternative`
--
ALTER TABLE `alternative`
  MODIFY `id_alternative` bigint(20) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=7;
--
-- AUTO_INCREMENT for table `donation`
--
ALTER TABLE `donation`
  MODIFY `id_donation` bigint(20) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=7;
--
-- AUTO_INCREMENT for table `message`
--
ALTER TABLE `message`
  MODIFY `id_message` bigint(20) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=14;
--
-- AUTO_INCREMENT for table `project`
--
ALTER TABLE `project`
  MODIFY `id_project` bigint(20) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=9;
--
-- AUTO_INCREMENT for table `project_has_user`
--
ALTER TABLE `project_has_user`
  MODIFY `id_project_user` bigint(20) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=7;
--
-- AUTO_INCREMENT for table `reply`
--
ALTER TABLE `reply`
  MODIFY `id_reply` bigint(20) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=2;
--
-- AUTO_INCREMENT for table `reward`
--
ALTER TABLE `reward`
  MODIFY `id_reward` bigint(20) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=12;
--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `id_user` bigint(20) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=5;
--
-- Constraints for dumped tables
--

--
-- Constraints for table `alternative`
--
ALTER TABLE `alternative`
  ADD CONSTRAINT `fk_al_proj` FOREIGN KEY (`id_project`) REFERENCES `project` (`id_project`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `donation`
--
ALTER TABLE `donation`
  ADD CONSTRAINT `fk_don_user` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `message`
--
ALTER TABLE `message`
  ADD CONSTRAINT `fk_msg_project` FOREIGN KEY (`id_project`) REFERENCES `project` (`id_project`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_msg_user` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `project`
--
ALTER TABLE `project`
  ADD CONSTRAINT `fk_admin` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `project_has_user`
--
ALTER TABLE `project_has_user`
  ADD CONSTRAINT `fk_project_user` FOREIGN KEY (`id_project`) REFERENCES `project` (`id_project`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_user_project` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `reply`
--
ALTER TABLE `reply`
  ADD CONSTRAINT `fk_reply_message` FOREIGN KEY (`id_message`) REFERENCES `message` (`id_message`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_reply_project` FOREIGN KEY (`id_project`) REFERENCES `project` (`id_project`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_reply_user` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `reward`
--
ALTER TABLE `reward`
  ADD CONSTRAINT `fk_project` FOREIGN KEY (`id_project`) REFERENCES `project` (`id_project`) ON DELETE CASCADE ON UPDATE CASCADE;