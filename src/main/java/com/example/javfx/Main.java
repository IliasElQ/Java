package com.example.javfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {
            // Chargement de la vue de la page de connexion
            Parent root = FXMLLoader.load(getClass().getResource("Login-page.fxml"));
            Scene scene = new Scene(root);

            // Définition de la scène dans la fenêtre principale
            stage.setScene(scene);
            stage.show();

            // Gestionnaire de fermeture de la fenêtre
            stage.setOnCloseRequest(event -> {
                event.consume(); // Empêche la fermeture automatique
                logout(stage);   // Appelle la méthode de déconnexion personnalisée
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Méthode pour la déconnexion
    public void logout(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Déconnexion");
        alert.setHeaderText("Vous êtes sur le point de vous déconnecter !");
        alert.setContentText("Voulez-vous enregistrer avant de quitter ?");

        // Attente de la réponse de l'utilisateur
        if (alert.showAndWait().get() == ButtonType.OK) {
            System.out.println("Vous avez été déconnecté avec succès.");
            stage.close(); // Fermeture de la fenêtre
        }
    }

    public static void main(String[] args){
        launch(args); // Lance l'application JavaFX
    }
}
