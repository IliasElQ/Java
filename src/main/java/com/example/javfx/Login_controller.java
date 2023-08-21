package com.example.javfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

import java.net.URL;
import java.util.ResourceBundle;

public class Login_controller implements Initializable {
    @FXML
    private TextField tf_addr;

    @FXML
    private TextField tf_pass;
    @FXML
    private Label lb_check;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    @FXML
    public void login(ActionEvent actionEvent) {
        Connection connection = Login_database.getCon();
        String query = "SELECT * from users where username = ? and password = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1,tf_addr.getText());
            ps.setString(2,tf_pass.getText());
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                /*lb_check.setText("Login Successfully");
                lb_check.setStyle("-fx-text-fill: blue;-fx-font-size: 1.3em;-fx-font-weight: bold");

                 */
                changeToMainScene();


            }else {
                lb_check.setText("Error");
                lb_check.setStyle("-fx-text-fill: #FF0000;");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
    private void changeToMainScene() {
        try {
            Parent mainSceneRoot = FXMLLoader.load(getClass().getResource("Scene1.fxml"));
            Scene mainScene = new Scene(mainSceneRoot);
            Stage stage = (Stage) tf_addr.getScene().getWindow(); // Get the current stage
            stage.setScene(mainScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}