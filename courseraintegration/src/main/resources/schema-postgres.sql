
******************** Coursera Schema ***********************************

CREATE SCHEMA courseraintegration_schema AUTHORIZATION root;

DROP SCHEMA IF EXISTS courseraintegration_schema;

******************** Program API Table ***********************************

DROP TABLE IF EXISTS courseraintegration_schema.program;

CREATE TABLE  courseraintegration_schema.program (
CONTENT_ID varchar(255),
TITLE VARCHAR(255) NOT NULL,
PROVIDER_ID VARCHAR(255) NOT NULL,
STATUS VARCHAR(20),
LAUNCH_URL varchar(255),
ID varchar(255) NOT NULL,
THUMBNAIL_URI varchar(255),
created_date Date,
PRIMARY KEY (ID)
);


******************** Content API Table ***********************************

DROP TABLE IF EXISTS courseraintegration_schema.content;

CREATE TABLE courseraintegration_schema.content(
CONTENT_ID varchar(255),
TITLE VARCHAR(255) NOT NULL,
PROVIDER_ID VARCHAR(255) NOT NULL,
STATUS VARCHAR(20),
DESCRIPTION varchar(2000),
LAUNCH_URL varchar(255),
PROGRAM_ID varchar(255) NOT NULL,
THUMBNAIL_URI varchar(255),
created_date Date,
PRIMARY KEY (CONTENT_ID)
);

******************** config Table ***********************************


DROP TABLE IF EXISTS courseraintegration_schema.config;

CREATE TABLE courseraintegration_schema.config(
org_id SERIAL PRIMARY KEY,
client_secret  varchar(255) NOT NULL ,
client_id varchar(255) NOT NULL ,
client_name varchar(255) NOT NULL,
contact_person varchar(255) NOT NULL,
email varchar(255) NOT NULL,
phone numeric,
lms_api_host varchar(255) NOT NULL ,
ftp_host varchar(255) NOT NULL,
ftp_user varchar(255) NOT NULL,
ftp_password varchar(255) NOT NULL);

******************** batchjobscheduler Table ***********************************

DROP TABLE IF EXISTS courseraintegration_schema.batchjobscheduler;

CREATE TABLE courseraintegration_schema.batchjobscheduler(
job1_schedule varchar(255),
job2_schedule varchar(255),
job3_schedule varchar(255),
job4_schedule varchar(255),
job5_schedule varchar(255),
job6_schedule varchar(255),
org_id INTEGER REFERENCES courseraintegration_schema.config(org_id),
PRIMARY KEY (org_id));








