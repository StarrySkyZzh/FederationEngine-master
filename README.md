# README #

The law enforcement environment has been dramatically changed in the past 20 years. In order to catch up with these changes and meet the challenges in the era of big data, the objective of the Integrated Law Enforcement project is to develop, integrate and demonstrate state-of-the-art capabilities in federated data management, entity resolution and federated analytics to provide law enforcement agencies and analysts with uniform and timely access to integrated information and analytical outcomes. To meet the objective, a set of cutting edge technologies will be comprised to effectively link all available data among internal information systems and external data sources and reveals the relationships among data segments of various natures and types, so that an overall image around the topic of interest can be quickly formed. 

### What is this repository for? ###

* This is the repository for codes that can facilitate a quickstart for building the federated data platform.

* Version 1.0POC

#### Description for Bitbucket Federated Data Platform Repository ####

* All the projects are developed as Maven projects using Eclipse Java EE IDE for Web Developers (version: Neon). To run these code, simply clone them into your own IDE and no further library need to be downloaded.

##### 1 Demo #####
* It contains the code for the first demonstration for AFP on 15/11/2016, which contains simple DB entity ingestion function from a postgresql server as PROMIS to ElasticSearch of a comprehensive Person entity containing case and location information.

##### 2 ElasticSearch #####
* It contains the code created by Dr Zaiwen Feng for initial ElasticSearch Java API study and ingestion tests.
  
##### 3 HDFS #####
* It contains IO examples of accessing HDFS via Java API.

##### 4 IntegratedProject #####
* It is an initial structure design for Data Lake components where no code is stored.

##### 5 JunitTest #####
* It is a project for studying Junit for unit testing. Contains a simple helloworld example.
	
##### 6 PostgreSQL #####
* It is a project for developing, testing and storing Postgresql related code (in src/main/java folder) and scripts (in src/main/sources folder). 

##### 7 PromisDataIngestion #####
* It contains the formal data ingestion implementation code for both db ingestion and hdfs file ingestion. The main entrance is Demo.java, where the parameters for db ingestion and hdfs ingestion are commented respectively along with annotations. Please adjust file paths accordingly to link log4j2 and schema mapping files with the project correctly.

##### 8 PromisUpdatePipeLine #####
* It contains the formal code for three components: log4j2 logging, postgresql PROMIS notification watcher and rabbitMQ watcher version 1. The main entrance of PROMIS watcher is Main.java

##### 9 RMQ #####
* It contains three RMQ tests based on the rabbitMQ official tutorials including helloworld, workqueues and routing. In the PromisUpdatePipeLine the rabbitMQ messages are sent using the routing style.

##### 10 RMQListener #####
* It contains the code for implementing the formal RMQ message receiving component in Spark, in which two java files describes the message parsing functions in MessageObject.java and the RMQ listener version 2, which parses the message and send the parsed object to process in spark.

##### 11 root #####
* It was designed to be the parent pom for all the maven projects, currently empty.

##### 12 Spark #####
* It contains the code for spark related code, currently including hdfs accessing functions, RMQ listener functions and two spark examples. Still developing.



### How do I get set up? ###

* Set up, Configuration & Deployment instructions can be found in the following document:

    [ Federated Process Infrastructure Volume 1 version 1.0POC - Testing Platform Installation ][testing platform install]

### Contact ###

ACRC, UniSA

Mawson lakes, SA 5067

Ph: +61 (08) 8302 3582 

ACRC.Enquiries@unisa.edu.au

www.unisa.edu.au

[testing platform install]: https://d2dcrc.atlassian.net/wiki/download/attachments/45907984/Initial%20Federated%20Process%20Infrastructure.docx?version=1&modificationDate=1476335556307&api=v2