-- phpMyAdmin SQL Dump
-- version 4.4.10
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Dec 19, 2015 at 02:26 PM
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
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `alternative`
--

INSERT INTO `alternative` (`id_alternative`, `description`, `n_votes`, `divisor`, `id_project`) VALUES
(36, 'ola', 0, 5, 48),
(37, 'dois tetes', 0, 5, 49),
(38, 'teste alternative 4', 0, 5, 50),
(43, 'teste alternative', 0, 5, 55),
(44, 'teste alternative', 0, 5, 56),
(49, 'teste alternative', 0, 5, 61),
(50, 'teste alternative', 0, 5, 62);

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
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `donation`
--

INSERT INTO `donation` (`id_donation`, `pledge_value`, `id_user`, `id_reward`, `id_alternative`, `id_project`) VALUES
(1, 150, 1, 6, 5, 3),
(2, 50, 3, 0, 6, 4),
(3, 1, 1, 0, 2, 1),
(4, 50, 1, 0, 0, 4),
(5, 50, 1, 0, 0, 4),
(6, 100, 1, 14, 10, 13),
(7, 5, 1, 13, 10, 13),
(8, 4, 1, 0, 10, 13),
(9, 7, 1, 45, 0, 43);

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

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
) ENGINE=InnoDB AUTO_INCREMENT=63 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `project`
--

INSERT INTO `project` (`id_project`, `name`, `description`, `limit_date`, `target_value`, `current_value`, `enterprise`, `id_user`, `accepted`, `post_id`, `base_hostname`) VALUES
(43, 'cenas', 'cenas maradas', '15/01/2016 16', 15000, 7, 'cenas lda', 1, 0, '', ''),
(44, 'teste', 'teste', '15/01/2016 12', 15000, 0, 'test lda', 1, 0, '', ''),
(45, 'cenas2', 'adf', '15/01/2016 16', 15000, 0, 'cenas', 1, 0, '', ''),
(46, 'teste2', 'asdaf', '15/01/2016 12', 15000, 0, 'test lad', 1, 0, '', ''),
(47, 'teste3', 'sfg', '15/01/2016 12', 15000, 0, 'teste lad', 1, 0, '', ''),
(48, 'teste4', 'fad', '16/12/2015 14', 15000, 0, 'test', 1, 0, '', ''),
(49, 'teste20', 'dasff', '15/12/2016 12', 12234, 0, 'test la', 1, 0, '', ''),
(50, 'cenas1000', 'cenas1000', '17/12/2015 16', 1234, 0, 'cenas lds', 1, 0, '', ''),
(55, 'teste4000', 'teste4000', '17/12/2015 16', 1234, 0, 'cenas lds', 1, 0, '', ''),
(56, 'teste5000', 'teste5000', '15/01/2016 16', 1234, 0, 'cenas lds', 1, 0, '', ''),
(61, 'teste project', 'teste project', '17/12/2015 16', 15000, 0, 'cenas lds', 1, 0, '135500781822', ''),
(62, 'teste project 2', 'teste project 2', '15/01/2016 16', 1234, 0, 'teste lda', 1, 0, '135500816637', '');

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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `project_has_user`
--

INSERT INTO `project_has_user` (`id_project_user`, `id_project`, `id_user`) VALUES
(1, 43, 1);

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `reward`
--

CREATE TABLE `reward` (
  `id_reward` bigint(20) NOT NULL,
  `description` varchar(200) COLLATE utf8_bin NOT NULL,
  `min_value` double NOT NULL,
  `id_project` bigint(20) DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=67 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `reward`
--

INSERT INTO `reward` (`id_reward`, `description`, `min_value`, `id_project`) VALUES
(45, 'uma cena', 5, 43),
(46, 'um teste', 5, 44),
(47, 'asd', 5, 44),
(48, '5', 5, 45),
(49, 'um teste', 5, 46),
(50, 'dois testes', 5, 46),
(51, 'um teste', 5, 47),
(52, 'um teste', 5, 48),
(53, 'um tetse', 5, 49),
(54, 'cenas rewards', 5, 50),
(59, 'um teste ', 5, 55),
(60, 'um teste ', 5, 56),
(65, 'cenas rewards', 5, 61),
(66, 'cenas rewards', 5, 62);

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
  `secret_token` varchar(255) COLLATE utf8_bin NOT NULL,
  `user_token` varchar(255) COLLATE utf8_bin NOT NULL,
  `tumblr_username` varchar(50) COLLATE utf8_bin DEFAULT NULL
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`id_user`, `name`, `password`, `bi`, `age`, `email`, `account_balance`, `is_tumblr_account`, `secret_token`, `user_token`, `tumblr_username`) VALUES
(1, 'joaoprg', '1234', '14354313', 22, 'joao@gmail.com', 9634, 0, '0528EKboLVvYDeBT9btIfSvyLkTWxf8SFwZhS0vDyy6ORb1nEZ', 'GNuzZETD6TMRogxrlaohoMMpzQtRTotzKh1ekCEf3oqbQPYDMO', 'joaoprg'),
(2, 'joao', 's', '1213242323', 22, 'subtil@gmail.com', 10150, 0, '', '0', NULL),
(3, 'amd', 'amd', '1234', 34, 'amd@amd.com', 100, 0, '', '0', ''),
(4, 'intel', 'intel', '12345678', 12, 'teste@teste.com', 100, 0, '', '0', ''),
(5, 'guida', 'maria', '45', 54, 'maria@g.com', 100, 0, '', '0', ''),
(6, 'ma', 'ma', '13434', 45, 'ma@ma.com', 100, 0, '', '0', ''),
(7, 'je', 'quinha', '3123245', 67, 'je@sapo.pt', 100, 0, '', '0', ''),
(8, 'marinho', 'aac', '768', 65, 'marinho@aac.pt', 100, 0, '', '0', '');

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
  MODIFY `id_alternative` bigint(20) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=51;
--
-- AUTO_INCREMENT for table `donation`
--
ALTER TABLE `donation`
  MODIFY `id_donation` bigint(20) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=10;
--
-- AUTO_INCREMENT for table `message`
--
ALTER TABLE `message`
  MODIFY `id_message` bigint(20) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `project`
--
ALTER TABLE `project`
  MODIFY `id_project` bigint(20) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=63;
--
-- AUTO_INCREMENT for table `project_has_user`
--
ALTER TABLE `project_has_user`
  MODIFY `id_project_user` bigint(20) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=2;
--
-- AUTO_INCREMENT for table `reply`
--
ALTER TABLE `reply`
  MODIFY `id_reply` bigint(20) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `reward`
--
ALTER TABLE `reward`
  MODIFY `id_reward` bigint(20) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=67;
--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `id_user` bigint(20) NOT NULL AUTO_INCREMENT,AUTO_INCREMENT=20;
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
