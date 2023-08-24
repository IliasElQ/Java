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
    private final ObservableList<Patient> patients = FXCollections.observableArrayList();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        detailsButtonColumn.setCellFactory(param -> new TableCell<>() {
            private final Button detailsButton = new Button("Details");

            {
                detailsButton.setOnAction(event -> {
                    Patient patient = getTableView().getItems().get(getIndex());

                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("patient_details.fxml"));
                        Parent root = loader.load();
                        Scene newScene = new Scene(root);

                        // Get the controller for the patient details scene
                        PatientDetailsController detailsController = loader.getController();
                        detailsController.initData(patient); // Pass the patient object

                        // Create a new stage for the new scene
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
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/patient_database", "root", "myroot2468");
            String sql = "SELECT * FROM patient";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
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
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tableView.setItems(patients);

    }

    @FXML
    void Button_on_click(ActionEvent event) {

        RadioButton selectedRadioButton = (RadioButton) genderToggleGroup.getSelectedToggle();
        String mygender = selectedRadioButton == null ? null : selectedRadioButton.getText();

        Patient patient = null;
        if (IdInput.getText().isEmpty() || NomInput.getText().isEmpty() || PrenomInput.getText().isEmpty() ||
                AgeInput.getText().isEmpty() || NumberInput.getText().isEmpty() || mygender == null ||
                T_of_maladie.getValue() == null || DateInput.getValue() == null) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Missing Information");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all the required fields.");
            alert.showAndWait();
        } else if (isNumeric(AgeInput.getText()) || isNumeric(NumberInput.getText())) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input");
            alert.setHeaderText(null);
            alert.setContentText("Please enter valid numeric values for Age and Phone fields.");
            alert.showAndWait();
        } else {
            java.time.LocalDate selectedDate = DateInput.getValue();
            if (selectedDate != null) {
                java.sql.Date date = java.sql.Date.valueOf(selectedDate);
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
                patients.add(patient);
                resetFields(null);
            } else {
\                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Missing Date");
                alert.setHeaderText(null);
                alert.setContentText("Please select a date.");
                alert.showAndWait();
            }
        }
        addPatientToDatabase(patient);

    }

    private boolean isNumeric(String str) {
        return !str.matches("-?\\d+(\\.\\d+)?");
    }

    private int clickCount = 0;
    private Patient lastClickedPatient = null;

    @FXML
    void rowClicked(MouseEvent event) {
        Patient clickedPatient = tableView.getSelectionModel().getSelectedItem();
        if (clickedPatient != null) {
            if (clickedPatient.equals(lastClickedPatient)) {
                clickCount++;
            } else {
                clickCount = 1;
                lastClickedPatient = clickedPatient;
            }

            if (clickCount == 4) {
                IdInput.setText(clickedPatient.getId_nat());
                NomInput.setText(clickedPatient.getName());
                PrenomInput.setText(clickedPatient.getPrenam());
                AgeInput.setText(String.valueOf(clickedPatient.getAge()));
                NumberInput.setText(String.valueOf(clickedPatient.getPhone()));
                DateInput.setValue(clickedPatient.getDate());

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


    @FXML
    void resetFields(ActionEvent event) {
        // Clear all text fields
        IdInput.clear();
        NomInput.clear();
        PrenomInput.clear();
        AgeInput.clear();
        NumberInput.clear();
        DateInput.setValue(null); 

        T_of_maladie.getSelectionModel().clearSelection();

        genderToggleGroup.selectToggle(null);
    }

    private boolean isInputValid() {
        if (IdInput.getText().isEmpty() || NomInput.getText().isEmpty() ||
                PrenomInput.getText().isEmpty() || AgeInput.getText().isEmpty() ||
                NumberInput.getText().isEmpty() || genderToggleGroup.getSelectedToggle() == null ||
                T_of_maladie.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Missing Information");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all the required fields.");
            alert.showAndWait();
            return false;
        } else if (isNumeric(AgeInput.getText()) || isNumeric(NumberInput.getText())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input");
            alert.setHeaderText(null);
            alert.setContentText("Please enter valid numeric values for Age and Phone fields.");
            alert.showAndWait();
            return false;
        }
        return true;
    }

    @FXML
    void updateButton_on_click(ActionEvent event) {
        Patient selectedPatient = tableView.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            if (isInputValid()) {
                selectedPatient.setId_nat(IdInput.getText());
                selectedPatient.setName(NomInput.getText());
                selectedPatient.setPrenam(PrenomInput.getText());
                selectedPatient.setAge(Integer.parseInt(AgeInput.getText()));
                selectedPatient.setPhone(Integer.parseInt(NumberInput.getText()));
                selectedPatient.setDate(DateInput.getValue());
                RadioButton selectedRadioButton = (RadioButton) genderToggleGroup.getSelectedToggle();
                String mygender = selectedRadioButton == null ? null : selectedRadioButton.getText();
                selectedPatient.setGender(mygender);
                selectedPatient.setType_maladie(T_of_maladie.getValue());

                tableView.refresh(); 
                resetFields(null);
            }
            updatePatientInDatabase(selectedPatient);


        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Patient Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a patient to update.");
            alert.showAndWait();
        }


    }

    @FXML
    void searchAction(ActionEvent event) {
        String query = searchInput.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            tableView.setItems(patients); 
        } else {
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

    @FXML
    void removeCustomer(ActionEvent event) {
        Patient selectedPatient = tableView.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            int selectedID = tableView.getSelectionModel().getSelectedIndex();
            tableView.getItems().remove(selectedID);
            removePatientFromDatabase(selectedPatient);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Patient Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a patient to remove.");
            alert.showAndWait();
        }
    }
    private void addPatientToDatabase(Patient patient) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/patient_database", "root", "myroot2468")) {
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


    private void updatePatientInDatabase(Patient patient) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/patient_database", "root", "myroot2468")) {
            connection.setAutoCommit(false); 

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

            preparedStatement.executeUpdate();

            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    private void removePatientFromDatabase(Patient patient) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/patient_database", "root", "myroot2468")) {
            String sql = "DELETE FROM patient WHERE id_nat=?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, patient.getId_nat());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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