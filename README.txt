AltC195 Salon Scheduler

Purpose

A desktop JavaFX application to manage salon clients, stylists, and appointment scheduling.

Author & Version

Author: Jean-Pierre Atiles
Contact: Student ID : 011028774
         Email: jatile1@wgu.edu
Student App Version: 1.0
Date: May 10, 2025

Development Environment

IDE: IntelliJ IDEA Community Edition 2024.3.3
JDK: Java SE 23.0
JavaFX: JavaFX SDK 17.0.6

Dependencies

MySQL Connector/J: mysql-connector-java-8.4.0

Database Setup

Install MySQL and create a schema named altdb.

Run the provided schema.sql and data.sql scripts to build tables and sample data.

Ensure a user altUser with password altPSW has appropriate privileges on altdb.

Running the Application

Make sure the MySQL server is running and credentials in JDBC.java match your setup.

From the IDE, run the Main class in the com.example.altc195project.main package, or use:

mvn clean javafx:run

The login screen will appear; authenticate with a valid user from the user table.

Additional Report

"Appointment Type by Month" Report:
This report queries the appt table to count appointments grouped by their type and the month of the start date.It helps visualize  trends and the popularity of different service types.Results are displayed in a TableView and can be extended to charts if desired.

