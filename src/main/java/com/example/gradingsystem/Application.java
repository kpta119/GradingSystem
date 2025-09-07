package com.example.gradingsystem;

import com.example.gradingsystem.dao.TestDAO;
import com.mongodb.client.MongoDatabase;
import io.github.cdimascio.dotenv.Dotenv;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        Dotenv dotenv = Dotenv.load();
        String uri = dotenv.get("MONGODB_URI");
        MongoConnector.init(uri);
        MongoDatabase database = MongoConnector.getInstance().getDatabase();
        TestDAO.init(database);
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("/com/example/gradingsystem/views/mainwindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1500, 1000);
        stage.setTitle("System for entering school results!");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}