package com.desctopbpmn.desctopbpmn;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

public class MainController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onConnectButtonClick() throws IOException {
        welcomeText.setText("Welcome to JavaFX Application!");
        Server server = new Server();
    }

    @FXML
    protected void onCancelButtonClick(){
    }
}