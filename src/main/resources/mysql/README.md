# MySQL Integration

## Overview

This document describes the set-up procedures to integrate MySQL to this application.

## Install

1. Install MySQL [here](https://dev.mysql.com/downloads/installer/).
1. Follow through with default settings.
   - You can set up the user and log in with your own credentials, this application will require a separate user that will be defined later on.

## Setup

1. Create a new database `rendr_db`.
   ``` sql
   CREATE DATABASE rendr_db;
   ```

1. Create a new user based on the username and password defined in `resources/application.properties` and associate the user to the database.
   ``` properties
   spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
   spring.jpa.hibernate.ddl-auto=update
   spring.datasource.url=jdbc:mysql://localhost:3306/rendr_db
   spring.datasource.username=rendrUser
   spring.datasource.password={PASSWORD} # actual password in file
   ```
   ``` sql
   CREATE USER 'rendrUser'@'localhost' IDENTIFIED BY 'PASSWORD';
   GRANT ALL PRIVILEGES ON rendr_db.* TO 'rendrUser'@'localhost';
   ```

1. Enter the database
   ``` sql
   USE rendr_db;
   ```

1. Run the various `SQL` scripts in `resources/mysql/scripts/` in the order given in the file names.

1. Display tables and select data from them to verify the data is present.
   ``` sql
   SHOW tables;
   ```

## Frequently Asked Questions (FAQ)

1. My database have a different tables and attributes from the updated version, and I am unable to drop the tables.
   - A simple solution is to switch off foreign constraints first then drop tables
   ``` sql
   SET FOREIGN_KEY_CHECKS = 0; -- switch off
   DROP TABLE IF EXISTS ...;   -- drop desired tables
   SET FOREIGN_KEY_CHECKS = 1; -- switch back on
   ```
   - Afterwards, rerun the initialisation scripts in `resources/mysql/scripts/`.

2. Why is there a 
   - Refer to this [StackExchange post](https://superuser.com/questions/1287992/mysqlinstaller-popping-up-unexpectedly) to turn off the scheduler.

3. Why is my MySQL instance is not connecting to localhost?
   - See this [StackOverflow post](https://stackoverflow.com/questions/32022580/failed-to-connect-to-mysql-at-localhost3306-with-user-roothttps://stackoverflow.com/questions/32022580/failed-to-connect-to-mysql-at-localhost3306-with-user-root) to start your SQL service.
