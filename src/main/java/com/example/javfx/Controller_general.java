package com.example.javfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class Controller_general implements Initializable {

    // Éléments FXML
    @FXML
    private TextField searchInput;
    @FXML
    private TextField IdInput;
    @FXML
    private TextField NumberInput;
    @FXML
    private TextField NomInput;
    @FXML
    private TextField PrenomInput;
    @FXML
    private TextField AgeInput;
    @FXML
    private DatePicker DateInput;
    @FXML
    private RadioButton maleRadioButton;
    @FXML
    private RadioButton femaleRadioButton;
    @FXML
    private TableColumn<Patient, Integer> AgeColumn;
    @FXML
    private TableColumn<Patient, Integer> Number_Column ;
    @FXML
    private TableColumn<Patient, String> GenderColumn;
    @FXML
    private TableColumn<Patient, LocalDate> DateColumn;
    @FXML
    private TableColumn<Patient, String> IdColumn;
    @FXML
    private TableColumn<Patient, String> PrenomColumn;
    @FXML
    private TableColumn<Patient, String> nomColumn;
    @FXML
    private TableColumn<Patient, String> DateColumn21;
    @FXML
    private ChoiceBox<String> T_of_maladie ;
    @FXML
    private ToggleGroup genderToggleGroup;
    @FXML
    private TableColumn<Patient, Void> detailsButtonColumn;
    @FXML
    private TableView<Patient> tableView;
    
    // Liste observable pour stocker les patients
    private final ObservableList<Patient> patients = FXCollections.observableArrayList();
    
    // Méthode d'initialisation appelée lors du chargement de la vue
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        
        // Configuration de la colonne "Détails" du tableau
        detailsButtonColumn.setCellFactory(param -> new TableCell<>() {
            private final Button detailsButton = new Button("Details");

            {
                detailsButton.setOnAction(event -> {
                    // Action lorsque le bouton "Détails" est cliqué
                    Patient patient = getTableView().getItems().get(getIndex());

                    try {
                        // Charger la vue des détails du patient
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("patient_details.fxml"));
                        Parent root = loader.load();
                        Scene newScene = new Scene(root);

                        // Obtenir le contrôleur de la vue des détails du patient
                        PatientDetailsController detailsController = loader.getController();
                        detailsController.initData(patient); // Passer l'objet patient

                        // Créer une nouvelle fenêtre pour la nouvelle scène
                        Stage newStage = new Stage();
                        newStage.setScene(newScene);
                        newStage.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty) {
                    setGraphic(detailsButton);
                } else {
                    setGraphic(null);
                }
            }
        });

        ObservableList<String> Typedemaladie = FXCollections.observableArrayList("Type A", "Type B", "Type C");
        T_of_maladie.setItems(Typedemaladie);
        IdColumn.setCellValueFactory(new PropertyValueFactory<>("Id_nat"));
        PrenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenam"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        AgeColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        DateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        Number_Column.setCellValueFactory(new PropertyValueFactory<>("phone"));
        GenderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        DateColumn21.setCellValueFactory(new PropertyValueFactory<>("type_maladie"));
        tableView.setItems(patients);
        // Récupération des données de la base de données et ajout dans la liste observable
        try {
            // Établir une connexion à la base de données
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/patient_database", "root", "myroot2468");
            String sql = "SELECT * FROM patient";
            // Préparer la requête SQL
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            
            // Exécuter la requête SQL et obtenir le résultat
            ResultSet resultSet = preparedStatement.executeQuery();

            // Parcourir le résultat et ajouter les patients dans la liste observable
            while (resultSet.next()) {
                Patient patient = new Patient(
                        resultSet.getString("id_nat"),
                        resultSet.getString("name"),
                        resultSet.getString("prenam"),
                        resultSet.getInt("age"),
                        resultSet.getInt("phone"),
                        resultSet.getDate("date").toLocalDate(), 
                        resultSet.getString("gender"),
                        resultSet.getString("type_maladie")
                );
                patients.add(patient);
            }
            
            // Fermer la connexion à la base de données
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tableView.setItems(patients);

    }

    @FXML
    void Button_on_click(ActionEvent event) {

        // Récupération du bouton radio sélectionné pour le genre
        RadioButton selectedRadioButton = (RadioButton) genderToggleGroup.getSelectedToggle();
        String mygender = selectedRadioButton == null ? null : selectedRadioButton.getText();

        // Initialisation d'un objet Patient
        Patient patient = null;
        // Vérification des champs requis et validation des entrées
        if (IdInput.getText().isEmpty() || NomInput.getText().isEmpty() || PrenomInput.getText().isEmpty() ||
                AgeInput.getText().isEmpty() || NumberInput.getText().isEmpty() || mygender == null ||
                T_of_maladie.getValue() == null || DateInput.getValue() == null) {

            // Affichage d'une alerte en cas de champs manquants
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Information manquante");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez remplir tous les champs obligatoires.");
            alert.showAndWait();
        } else if (isNumeric(AgeInput.getText()) || isNumeric(NumberInput.getText())) {
            
            // Affichage d'une alerte en cas de valeurs non numériques
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Entrée invalide");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez entrer des valeurs numériques valides pour l'âge et le numéro de téléphone.");
            alert.showAndWait();
        } else {
            
            // Conversion de la date sélectionnée en objet LocalDate
            java.time.LocalDate selectedDate = DateInput.getValue();
            if (selectedDate != null) {
                
                // Conversion de la date LocalDate en java.sql.Date
                java.sql.Date date = java.sql.Date.valueOf(selectedDate);

                // Création d'un nouvel objet Patient avec les valeurs saisies
                patient = new Patient(
                        IdInput.getText(),
                        NomInput.getText(),
                        PrenomInput.getText(),
                        Integer.parseInt(AgeInput.getText()),
                        Integer.parseInt(NumberInput.getText()),
                        date.toLocalDate(), 
                        mygender,
                        T_of_maladie.getValue()
                );

                // Ajout du patient à la liste et réinitialisation des champs
                patients.add(patient);
                resetFields(null);
            } else {

                // Affichage d'une alerte en cas de date manquante
\                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Date manquante");
                alert.setHeaderText(null);
                alert.setContentText("Veuillez sélectionner une date.");
                alert.showAndWait();
            }
        }

        // Ajout du patient à la base de données
        addPatientToDatabase(patient);

    }
    
    // Méthode utilitaire pour vérifier si une chaîne est numérique
    private boolean isNumeric(String str) {
        return !str.matches("-?\\d+(\\.\\d+)?");
    }

    // Compteur de clics et dernier patient cliqué
    private int clickCount = 0;
    private Patient lastClickedPatient = null;

    @FXML
    void rowClicked(MouseEvent event) {
        // Récupération du patient sélectionné
        Patient clickedPatient = tableView.getSelectionModel().getSelectedItem();
        if (clickedPatient != null) {
            if (clickedPatient.equals(lastClickedPatient)) {
                clickCount++;
            } else {
                clickCount = 1;
                lastClickedPatient = clickedPatient;
            }

            if (clickCount == 4) {

                // Affichage des détails du patient lors de 4 clics successifs
                IdInput.setText(clickedPatient.getId_nat());
                NomInput.setText(clickedPatient.getName());
                PrenomInput.setText(clickedPatient.getPrenam());
                AgeInput.setText(String.valueOf(clickedPatient.getAge()));
                NumberInput.setText(String.valueOf(clickedPatient.getPhone()));
                DateInput.setValue(clickedPatient.getDate());

                // Sélection du genre et du type de maladie du patient
                if (clickedPatient.getGender().equals("male")) {
                    maleRadioButton.setSelected(true);
                } else if (clickedPatient.getGender().equals("female")) {
                    femaleRadioButton.setSelected(true);
                }
                T_of_maladie.setValue(clickedPatient.getType_maladie());

                clickCount = 0; 
            }
        }
    }

    // Réinitialisation des champs de saisie
    @FXML
    void resetFields(ActionEvent event) {
        
        // Effacer tous les champs de texte
        IdInput.clear();
        NomInput.clear();
        PrenomInput.clear();
        AgeInput.clear();
        NumberInput.clear();
        DateInput.setValue(null); 

        // Effacer la sélection dans la ChoiceBox et les boutons radio
        T_of_maladie.getSelectionModel().clearSelection();

        genderToggleGroup.selectToggle(null);
    }

    // Vérification de la validité des données saisies
    private boolean isInputValid() {
        if (IdInput.getText().isEmpty() || NomInput.getText().isEmpty() ||
                PrenomInput.getText().isEmpty() || AgeInput.getText().isEmpty() ||
                NumberInput.getText().isEmpty() || genderToggleGroup.getSelectedToggle() == null ||
                T_of_maladie.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Information manquante");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez remplir tous les champs obligatoires.");
            alert.showAndWait();
            return false;
        } else if (isNumeric(AgeInput.getText()) || isNumeric(NumberInput.getText())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Entrée invalide");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez entrer des valeurs numériques valides pour l'âge et le numéro de téléphone.");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    // Mise à jour des informations d'un patient existant
    @FXML
    void updateButton_on_click(ActionEvent event) {
        Patient selectedPatient = tableView.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            if (isInputValid()) {

                // Mise à jour des informations du patient sélectionné
                selectedPatient.setId_nat(IdInput.getText());
                selectedPatient.setName(NomInput.getText());
                selectedPatient.setPrenam(PrenomInput.getText());
                selectedPatient.setAge(Integer.parseInt(AgeInput.getText()));
                selectedPatient.setPhone(Integer.parseInt(NumberInput.getText()));
                selectedPatient.setDate(DateInput.getValue());

                // Mise à jour du genre et du type de maladie
                RadioButton selectedRadioButton = (RadioButton) genderToggleGroup.getSelectedToggle();
                String mygender = selectedRadioButton == null ? null : selectedRadioButton.getText();
                selectedPatient.setGender(mygender);
                selectedPatient.setType_maladie(T_of_maladie.getValue());

                // Rafraîchissement de la vue du tableau et réinitialisation des champs
                tableView.refresh(); 
                resetFields(null);
            }

            // Mise à jour du patient dans la base de données
            updatePatientInDatabase(selectedPatient);


        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucun patient sélectionné");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un patient à mettre à jour.");
            alert.showAndWait();
        }


    }

    // Recherche de patients en fonction des critères saisis
    @FXML
    void searchAction(ActionEvent event) {

        // Récupération de la requête de recherche
        String query = searchInput.getText().trim().toLowerCase();
        if (query.isEmpty()) {

            // Affichage de tous les patients en cas de requête vide
            tableView.setItems(patients); 
        } else {

            // Filtrage des patients en fonction de la requête de recherche
            ObservableList<Patient> filteredPatients = patients.filtered(patient ->
                    patient.getId_nat().toLowerCase().contains(query) ||
                            patient.getName().toLowerCase().contains(query) ||
                            patient.getPrenam().toLowerCase().contains(query) ||
                            patient.getGender().toLowerCase().contains(query) ||
                            patient.getType_maladie().toLowerCase().contains(query)
            );
            tableView.setItems(filteredPatients);
        }
    }

    // Suppression d'un patient de la liste et de la base de données
    @FXML
    void removeCustomer(ActionEvent event) {
        Patient selectedPatient = tableView.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {

            // Récupération de l'indice du patient sélectionné
            int selectedID = tableView.getSelectionModel().getSelectedIndex();

            // Suppression du patient de la liste
            tableView.getItems().remove(selectedID);

            // Suppression du patient de la base de données
            removePatientFromDatabase(selectedPatient);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Aucun patient sélectionné");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner un patient à supprimer.");
            alert.showAndWait();
        }
    }

    // Ajout d'un patient à la base de données
    private void addPatientToDatabase(Patient patient) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/patient_database", "root", "myroot2468")) {

            // Préparation de la requête SQL d'insertion
            String sql = "INSERT INTO patient (id_nat, name, prenam, age, phone, date, gender, type_maladie) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, patient.getId_nat());
            preparedStatement.setString(2, patient.getName());
            preparedStatement.setString(3, patient.getPrenam());
            preparedStatement.setInt(4, patient.getAge());
            preparedStatement.setInt(5, patient.getPhone());
            preparedStatement.setDate(6, java.sql.Date.valueOf(patient.getDate()));
            preparedStatement.setString(7, patient.getGender());
            preparedStatement.setString(8, patient.getType_maladie());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Mise à jour d'un patient dans la base de données
    private void updatePatientInDatabase(Patient patient) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/patient_database", "root", "myroot2468")) {

            // Désactivation de la validation automatique et mise à jour
            connection.setAutoCommit(false);

            // Préparation de la requête SQL de mise à jour
            String sql = "UPDATE patient SET name=?, prenam=?, age=?, phone=?, date=?, gender=?, type_maladie=? WHERE id_nat=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, patient.getName());
            preparedStatement.setString(2, patient.getPrenam());
            preparedStatement.setInt(3, patient.getAge());
            preparedStatement.setInt(4, patient.getPhone());
            preparedStatement.setDate(5, java.sql.Date.valueOf(patient.getDate()));
            preparedStatement.setString(6, patient.getGender());
            preparedStatement.setString(7, patient.getType_maladie());
            preparedStatement.setString(8, patient.getId_nat());


            // Exécution de la mise à jour et validation
            preparedStatement.executeUpdate();
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Suppression d'un patient de la base de données
    private void removePatientFromDatabase(Patient patient) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/patient_database", "root", "myroot2468")) {

            // Préparation de la requête SQL de suppression
            String sql = "DELETE FROM patient WHERE id_nat=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, patient.getId_nat());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Changement de scène vers Scene1.fxml
    private Stage stage;
    private Scene scene;
    private Parent root;

    public void switchToScene3(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Scene1.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }



}
