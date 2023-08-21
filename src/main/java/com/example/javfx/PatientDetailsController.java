package com.example.javfx;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PatientDetailsController implements Initializable {

    private Patient selectedPatient;


    @FXML
    private Button Add_buttom;
    @FXML
    private Label nameLabel;

    @FXML
    private Label ageLabel;

    @FXML
    private DatePicker date1_Input;

    @FXML
    private DatePicker date2_Input;

    @FXML
    private TableView <PatientDetails> detailsTableView;

    @FXML
    private TextField rem_Input;

    @FXML
    private TableColumn<PatientDetails, String> remarque_Column;
    @FXML
    private TableColumn<PatientDetails, LocalDate> date2_Column;
    @FXML
    private TableColumn<PatientDetails, LocalDate> date1_Column;

    private String jdbcUrl = "jdbc:mysql://localhost:3306/patient_details";
    private String username = "root";
    private String password = "myroot2468";


    private Patient patient;

    private int patientID;

    private String patientIdNatio;

    @FXML
    private VBox notificationBox;

    @FXML
    private VBox imageRoot;

    @FXML
    private Button uploadImageButton;

    @FXML
    private TextField imageNameField;

    @FXML
    private ListView<String> imageListView;

    @FXML
    private TextField newImageNameField;

    @FXML
    private Button renameImageButton;

    @FXML
    private ImageView loadedImageView;



    @FXML
    private Button addImageButton;

    @FXML
    private TabPane tabPane;




    @FXML
    private TableView<Patient> todayAppointmentsTableView;
    @FXML
    private TableColumn<Patient, String> patientNameColumn;

    private final ObservableList<PatientDetails> detailsTableViews = FXCollections.observableArrayList(
            new PatientDetails(LocalDate.of(1995, 8, 10),"ilias",LocalDate.of(1995, 8, 10))
    );





    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if (patient != null) {
            System.out.println("Patient data available: " + patient.getName());
            nameLabel.setText("Patient Name: " + patient.getName());
        } else {
            System.out.println("No patient data available");
        }


        date1_Column.setCellValueFactory(new PropertyValueFactory<PatientDetails , LocalDate>("date1"));
        remarque_Column.setCellValueFactory(new PropertyValueFactory<PatientDetails , String>("remarque"));
        date2_Column.setCellValueFactory(new PropertyValueFactory<PatientDetails , LocalDate>("date2"));
        detailsTableView.setItems(detailsTableViews);

        loadPatientDetailsFromDatabase();
        detailsTableView.setItems(detailsTableViews);


        // Set up event handler for the "Add Image" button

        // Load image names from the database and populate the list view
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            // Check if the newTab is the tab containing image adding functionality
            if (newTab.getId().equals("imageTab")) {
                // Load image names from the database and populate the list view
                loadImages();
            }
        });

        // Load image names from the database and populate the list view
        loadImages();








    }

    public void initData(Patient patient) {
        System.out.println("Initializing DetailsViewController with patient: " + patient.getName());
        this.patient = patient;
        this.patientIdNatio = patient.getId_nat(); // Assign the value directly as a string
        loadPatientDetailsFromDatabase();
    }




    /*public void initData(Patient patient) {
        System.out.println("Initializing DetailsViewController with patient: " + patient.getName());
        this.patient = patient;

        this.patientIdNatio = patient.getId_nat();
        this.patientID = Integer.parseInt(patient.getId_nat());
        loadPatientDetailsFromDatabase();
    }

     */



    @FXML
    void Add_button(ActionEvent event) {
        LocalDate selectedDate1 = date1_Input.getValue();
        LocalDate selectedDate2 = date2_Input.getValue();
        String remarque = rem_Input.getText().trim(); // Trim whitespace

        if (selectedDate1 == null || selectedDate2 == null || remarque.isEmpty()) {
            // Show an alert if any of the required fields are missing
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Incomplete Fields");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all the required fields.");
            alert.showAndWait();
            return; // Stop the process
        }

        java.sql.Date date1_I = java.sql.Date.valueOf(selectedDate1);
        java.sql.Date date2_I = java.sql.Date.valueOf(selectedDate2);

        String patientIdNatio = patient.getId_nat(); // Get the patient's unique ID
        PatientDetails patientDetails = new PatientDetails(date1_I.toLocalDate(), remarque, date2_I.toLocalDate());

        savePatientDetailsToDatabase(patientDetails, patientIdNatio);

        detailsTableViews.add(patientDetails);
        clearInputFields();
    }





    private void loadPatientDetailsFromDatabase() {
        detailsTableViews.clear(); // Clear existing data before loading

        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            String selectQuery = "SELECT * FROM patient_details WHERE patient_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            preparedStatement.setString(1, patientIdNatio); // Set the patient's ID or unique identifier

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                LocalDate date1 = resultSet.getDate("date1").toLocalDate();
                String remarque = resultSet.getString("remarque");
                LocalDate date2 = resultSet.getDate("date2").toLocalDate();

                PatientDetails patientDetails = new PatientDetails(date1, remarque, date2);
                detailsTableViews.add(patientDetails);
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }





    public void savePatientDetailsToDatabase(PatientDetails patientDetails, String patientIdNatio) {
        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);

            String insertQuery = "INSERT INTO patient_details (patient_id, date1, remarque, date2) VALUES (?, ?, ?, ?)";
            PreparedStatement insertStatement = connection.prepareStatement(insertQuery);

            insertStatement.setString(1, patientIdNatio);
            insertStatement.setDate(2, java.sql.Date.valueOf(patientDetails.getDate1()));
            insertStatement.setString(3, patientDetails.getRemarque());
            insertStatement.setDate(4, java.sql.Date.valueOf(patientDetails.getDate2()));

            insertStatement.executeUpdate();
            insertStatement.close();

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }








    private void clearInputFields() {
        rem_Input.clear();
        date1_Input.setValue(null);
        date2_Input.setValue(null);
    }

    /*



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

     */


    @FXML
    private ImageView imageUploaderView;

    @FXML
    private void chooseImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");
        File selectedFile = fileChooser.showOpenDialog(imageUploaderView.getScene().getWindow());

        if (selectedFile != null) {
            // Display the selected image in the ImageView
            Image selectedImage = new Image(selectedFile.toURI().toString());
            imageUploaderView.setImage(selectedImage);
        }
    }




    @FXML
    private void uploadImage(ActionEvent event) {
        Image selectedImage = imageUploaderView.getImage();
        String newImageName = newImageNameField.getText().trim();

        if (selectedImage != null && !newImageName.isEmpty()) {
            try {
                saveImageToDatabase(selectedImage, newImageName);
                clearImageListAndRefresh(); // Refresh the image list view after adding a new image
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Image Upload Error", "An error occurred while uploading the image.");
            }
        } else {
            showAlert("Image Upload Error", "Please select an image and enter a name.");
        }
    }

    private void clearImageListAndRefresh() {
        // Clear the current list of image names
        imageListView.getItems().clear();

        // Load image names from the database and populate the list view again
        loadImages();
    }



    private void saveImageToDatabase(Image image, String imageName) throws IOException {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            String insertQuery = "INSERT INTO patient_images (patient_id_natio, image_name, image_data) VALUES (?, ?, ?)";
            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                insertStatement.setString(1, patientIdNatio);
                insertStatement.setString(2, imageName);

                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                ImageIO.write(bufferedImage, "png", outputStream);
                InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
                insertStatement.setBinaryStream(3, inputStream);

                insertStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
    private List<String> getImageNamesFromDatabase() {
        List<String> imageNames = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            String selectQuery = "SELECT image_name FROM patient_images WHERE patient_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            preparedStatement.setString(1, patientIdNatio);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String imageName = resultSet.getString("image_name");
                imageNames.add(imageName);
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return imageNames;
    }

     */

    private List<String> getImageNamesFromDatabase() {
        List<String> imageNames = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            String selectQuery = "SELECT image_name FROM patient_images WHERE patient_id_natio = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            preparedStatement.setString(1, patientIdNatio);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String imageName = resultSet.getString("image_name");
                imageNames.add(imageName);
            }

            resultSet.close();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return imageNames;
    }



    private void loadImages() {
        List<String> imageNames = getImageNamesFromDatabase();

        // Populate the ListView with image names
        imageListView.getItems().clear();
        imageListView.getItems().addAll(imageNames);

        // Set up an event handler for selecting an image from the list
        imageListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Load and display the selected image
                Image selectedImage = loadImageFromDatabase(newValue);
                if (selectedImage != null) {
                    loadedImageView.setImage(selectedImage);
                } else {
                    // Handle the case where the selected image could not be loaded
                    loadedImageView.setImage(null); // Clear the image view
                    showAlert("Image Load Error", "Unable to load the selected image.");
                }
            } else {
                // No image selected, clear the image view
                loadedImageView.setImage(null);
            }
        });
    }


    private Image loadImageFromDatabase(String imageName) {

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            String selectQuery = "SELECT image_data FROM patient_images WHERE patient_id_natio = ? AND image_name = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                preparedStatement.setString(1, patientIdNatio);
                preparedStatement.setString(2, imageName);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        InputStream inputStream = resultSet.getBinaryStream("image_data");
                        return new Image(inputStream);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }



    /*
    private Image loadImageFromDatabase(String imageName) {

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            String selectQuery = "SELECT image_data FROM patient_images WHERE patient_id_natio = ? AND image_name = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                preparedStatement.setString(1, patientIdNatio);
                preparedStatement.setString(2, imageName);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        InputStream inputStream = resultSet.getBinaryStream("image_data");
                        return new Image(inputStream);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

     */


    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }





}


