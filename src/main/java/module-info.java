module com.example.gradingsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires com.fasterxml.jackson.databind;
    requires json.simple;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.bson;
    requires org.mongodb.driver.core;
    requires java.dotenv;

    opens com.example.gradingsystem to javafx.fxml, com.fasterxml.jackson.databind;
    opens com.example.gradingsystem.datamodel to com.fasterxml.jackson.databind;
    exports com.example.gradingsystem;
    exports com.example.gradingsystem.controllers;
    opens com.example.gradingsystem.controllers to com.fasterxml.jackson.databind, javafx.fxml;
}