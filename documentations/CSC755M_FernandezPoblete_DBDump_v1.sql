CREATE SCHEMA db_genesched;
USE db_genesched;

CREATE TABLE gs_user (
	id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(45) UNIQUE NOT NULL,
    password CHAR(60) NOT NULL,
    fName VARCHAR(45) NOT NULL,
    lName VARCHAR(45) NOT NULL,
    email VARCHAR(45) NOT NULL,
	dateAdded TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status TINYINT(1) NOT NULL DEFAULT 1
) engine = innoDB;

CREATE TABLE gs_venue (
	id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(60) NOT NULL UNIQUE,
	dateAdded TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status TINYINT(1) NOT NULL DEFAULT 1
) engine = innoDB;

CREATE TABLE gs_target_group (
	id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(60) NOT NULL UNIQUE,
	dateAdded TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status TINYINT(1) NOT NULL DEFAULT 1
) engine = innoDB;

CREATE TABLE gs_session (
	id INT PRIMARY KEY AUTO_INCREMENT,
    userID INT NOT NULL,
    startDate DATE,
    endDate DATE,
	dateAdded TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status TINYINT(1) NOT NULL DEFAULT 1,
    CONSTRAINT sessionfk_1
		FOREIGN KEY(userID)
        REFERENCES gs_user(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) engine = innoDB;

CREATE TABLE gs_blackdate (
	id INT PRIMARY KEY AUTO_INCREMENT,
    sessionID INT NOT NULL,
    blackdate DATE NOT NULL,	
	dateAdded TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status TINYINT(1) NOT NULL DEFAULT 1,
    CONSTRAINT blackdatefk_1
		FOREIGN KEY(sessionID)
        REFERENCES gs_session(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) engine = innoDB;

CREATE TABLE gs_blacktime (
	id INT PRIMARY KEY AUTO_INCREMENT,
    sessionID INT NOT NULL,
    starttime TIME NOT NULL,	
    endtime TIME NOT NULL,
	dateAdded TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status TINYINT(1) NOT NULL DEFAULT 1,
    CONSTRAINT blacktimefk_1
		FOREIGN KEY(sessionID)
        REFERENCES gs_session(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) engine = innoDB;

CREATE TABLE gs_activity (
	id INT PRIMARY KEY AUTO_INCREMENT,
    sessionID INT NOT NULL,
    venueID INT NOT NULL,
    name VARCHAR(60) NOT NULL UNIQUE,
    length INT NOT NULL,
    startTimeRange TIME,
    endTimeRange TIME,
	dateAdded TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status TINYINT(1) NOT NULL DEFAULT 1,
    CONSTRAINT activityfk_1
		FOREIGN KEY (sessionID)
        REFERENCES gs_session(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
	CONSTRAINT activityfk_2
		FOREIGN KEY (venueID)
        REFERENCES gs_venue(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE        
) engine = innoDB;

CREATE TABLE gs_activity_date (
	id INT PRIMARY KEY AUTO_INCREMENT,
    actID INT NOT NULL,
    actDate DATE NOT NULL UNIQUE,
	dateAdded TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status TINYINT(1) NOT NULL DEFAULT 1,
    CONSTRAINT actdatefk_1
		FOREIGN KEY(actID)
        REFERENCES gs_activity(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) engine = innoDB;

CREATE TABLE gs_activity_target_group (
	groupID INT NOT NULL,
    actID INT NOT NULL,
    actDate DATE NOT NULL UNIQUE,
	dateAdded TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY(groupID,actID),
    CONSTRAINT actgroupfk_1
		FOREIGN KEY(groupID)
        REFERENCES gs_target_group(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
	CONSTRAINT actgroupfk_2
		FOREIGN KEY(actID)
        REFERENCES gs_activity(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
) engine = innoDB;