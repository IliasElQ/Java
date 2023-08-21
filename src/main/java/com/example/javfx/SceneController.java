package com.example.javfx;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SceneController implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;

    private String jdbcUrl = "jdbc:mysql://localhost:3306/patient_details";
    private String username = "root";
    private String password = "myroot2468";

    @FXML
    private VBox notificationBox;

    public void switchToScene1(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("Scene1.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToScene2(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Scene2.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToScene3(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Canlendar.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

    public void switchToScene4(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Canlendar.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }




    private void loadNotificationsFromDatabase() {
        List<PatientDetails> todayNotifications = getNotificationsFromDatabase(LocalDate.now());

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        for (PatientDetails details : todayNotifications) {
            Label notificationLabel = new Label("Notification for " + details.getRemarque() +
                    " on " + details.getDate2().format(dateFormatter));
            notificationLabel.setStyle("-fx-background-color: #333; -fx-text-fill: white; -fx-padding: 10px;");
            notificationLabel.setWrapText(true);
            notificationBox.getChildren().add(notificationLabel);
        }
    }

    private List<PatientDetails> getNotificationsFromDatabase(LocalDate today) {
        List<PatientDetails> todayNotifications = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            String selectQuery = "SELECT * FROM patient_details WHERE DATE(date2) = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            preparedStatement.setDate(1, java.sql.Date.valueOf(today));

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                LocalDate date2 = resultSet.getDate("date2").toLocalDate();
                String remarque = resultSet.getString("remarque");

                PatientDetails patientDetails = new PatientDetails(null, remarque, date2);
                todayNotifications.add(patientDetails);
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return todayNotifications;
    }













    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadNotificationsFromDatabase();
    }
}