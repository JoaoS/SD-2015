-- phpMyAdmin SQL Dump
-- version 4.4.10
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: Oct 09, 2015 at 11:44 PM
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
  `multiplier` tinyint(4) NOT NULL,
  `id_project` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

--
-- Triggers `alternative`
--
DELIMITER $$
CREATE TRIGGER `check_multiplier` BEFORE INSERT ON `alternative`
 FOR EACH ROW BEGIN
	IF new.multiplier < 1 THEN
    	SIGNAL SQLSTATE '45000'
        	SET MESSAGE_TEXT = 'Multiplier must be greater or equal than 1';
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
  `pledge_value` bigint(20) NOT NULL,
  `id_user` bigint(20) DEFAULT NULL,
  `id_reward` bigint(20) DEFAULT NULL,
  `id_alternative` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

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
  `id_user` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

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
  `id_project` bigint(20) NOT NULL,
  `id_user` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- --------------------------------------------------------

--
-- Table structure for table `reward`
--

CREATE TABLE `reward` (
  `id_reward` bigint(20) NOT NULL,
  `description` varchar(200) COLLATE utf8_bin NOT NULL,
  `min_value` bigint(20) NOT NULL,
  `id_project` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

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
  `password` varchar(50) COLLATE utf8_bin NOT NULL,
  `bi` varchar(25) COLLATE utf8_bin NOT NULL,
  `age` tinyint(4) DEFAULT NULL,
  `email` varchar(30) COLLATE utf8_bin NOT NULL,
  `account_balance` bigint(20) DEFAULT '100'
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

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
  ADD KEY `fk_admin` (`id_user`);

--
-- Indexes for table `project_has_user`
--
ALTER TABLE `project_has_user`
  ADD PRIMARY KEY (`id_project`,`id_user`),
  ADD KEY `fk_user_project` (`id_user`);

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
  MODIFY `id_alternative` bigint(20) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `donation`
--
ALTER TABLE `donation`
  MODIFY `id_donation` bigint(20) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `message`
--
ALTER TABLE `message`
  MODIFY `id_message` bigint(20) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `project`
--
ALTER TABLE `project`
  MODIFY `id_project` bigint(20) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `reward`
--
ALTER TABLE `reward`
  MODIFY `id_reward` bigint(20) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `id_user` bigint(20) NOT NULL AUTO_INCREMENT;
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
  ADD CONSTRAINT `fk_alternative` FOREIGN KEY (`id_alternative`) REFERENCES `alternative` (`id_alternative`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_don_user` FOREIGN KEY (`id_user`) REFERENCES `user` (`id_user`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_reward` FOREIGN KEY (`id_reward`) REFERENCES `reward` (`id_reward`) ON DELETE CASCADE ON UPDATE CASCADE;

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
-- Constraints for table `reward`
--
ALTER TABLE `reward`
  ADD CONSTRAINT `fk_project` FOREIGN KEY (`id_project`) REFERENCES `project` (`id_project`) ON DELETE CASCADE ON UPDATE CASCADE;
