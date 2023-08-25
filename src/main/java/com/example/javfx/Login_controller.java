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

    // Références aux éléments du formulaire
    @FXML
    private TextField tf_addr;

    @FXML
    private TextField tf_pass;

    @FXML
    private Label lb_check;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Méthode d'initialisation appelée lors du chargement du formulaire
        // Vous pouvez ajouter du code d'initialisation ici si nécessaire
    }

    @FXML
    public void login(ActionEvent actionEvent) {
        // Méthode appelée lors de la tentative de connexion

        // Récupère la connexion à la base de données depuis une classe de gestion
        Connection connection = Login_database.getCon();

        // Requête SQL pour vérifier les informations d'identification de l'utilisateur
        String query = "SELECT * from users where username = ? and password = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1,tf_addr.getText());
            ps.setString(2,tf_pass.getText());
            ResultSet rs = ps.executeQuery();

            if (rs.next()){
                // L'utilisateur est authentifié, passer à la scène principale
                changeToMainScene();
            } else {
                // L'utilisateur n'est pas authentifié, affiche un message d'erreur
                lb_check.setText("Error");
                lb_check.setStyle("-fx-text-fill: #FF0000;");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Méthode pour changer vers la scène principale
    private void changeToMainScene() {
        try {
            // Charge la vue de la scène principale
            Parent mainSceneRoot = FXMLLoader.load(getClass().getResource("Scene1.fxml"));
            Scene mainScene = new Scene(mainSceneRoot);

            // Obtient la référence de la fenêtre actuelle
            Stage stage = (Stage) tf_addr.getScene().getWindow();

            // Définit la scène principale comme scène active dans la fenêtre
            stage.setScene(mainScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
