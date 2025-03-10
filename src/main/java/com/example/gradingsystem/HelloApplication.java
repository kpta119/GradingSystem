package com.example.gradingsystem;

import com.example.gradingsystem.datamodel.GradingSystemData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("mainwindow.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1500, 1000);
        stage.setTitle("System do wprowadzania wyników!");
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop(){
        try {
            GradingSystemData.getInstance().writeTestItems();
        } catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void init() throws Exception{
        try {
            GradingSystemData.getInstance().loadTestItems();
        } catch(IOException e){
            System.out.println(e.getMessage());
        }
    }
}