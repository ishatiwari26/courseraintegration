
******************** Coursera Schema ***********************************

CREATE SCHEMA courseraintegration_schema AUTHORIZATION root;

DROP SCHEMA IF EXISTS courseraintegration_schema;

******************** Program API Table ***********************************

DROP TABLE IF EXISTS courseraintegration_schema.program;

CREATE TABLE  courseraintegration_schema.program (
    name varchar(255) ,
    tagline varchar(255) ,
    content_id varchar(255) NOT NULL,
    content_type varchar(255) ,
    program_id varchar(255) NOT NUll,
	url varchar(255) ,
	created_date Date,
    PRIMARY KEY (program_id)
);

******************** Content API Table ***********************************

DROP TABLE IF EXISTS courseraintegration_schema.content;

CREATE TABLE courseraintegration_schema.content(
name varchar(255) ,
logo_url varchar(255) ,
content_id varchar(255) not Null,
description text ,
content_url varchar(255) ,
program_id varchar(255) NOT NULL,
language_code varchar(255) ,
type_name varchar(255) ,
content_type varchar(255) ,
tagline varchar(255) ,
photo_url varchar(255) ,
created_date Date,
PRIMARY KEY (content_id)
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







