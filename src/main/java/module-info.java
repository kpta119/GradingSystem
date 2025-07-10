module com.example.gradingsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.bson;
    requires org.mongodb.driver.core;
    requires java.dotenv;
    requires java.desktop;
    requires annotations;

    exports com.example.gradingsystem;
    opens com.example.gradingsystem.controllers to  javafx.fxml;
}