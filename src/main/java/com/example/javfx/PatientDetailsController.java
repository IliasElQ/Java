// Importations des classes nécessaires
package com.example.javfx;

// Importation des bibliothèques JavaFX et Java nécessaires
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


// Déclaration de la classe, implémente Initializable pour gérer l'initialisation de l'interface
public class PatientDetailsController implements Initializable {

    private Patient selectedPatient;
// Éléments de l'interface définis dans le fichier FXML, annotés avec @FXML
    // Ces éléments sont liés aux composants de l'interface graphique

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
    
    // URL JDBC et informations d'authentification à la base de données
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




    // Initialisation des composants d'interface et des gestionnaires d'événements
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialisations et gestionnaires d'événements pour les composants de l'interface

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


        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab.getId().equals("imageTab")) {
                loadImages();
            }
        });

        loadImages();



    }
    // Méthode pour initialiser les données du patient
    public void initData(Patient patient) {
        System.out.println("Initializing DetailsViewController with patient: " + patient.getName());
        this.patient = patient;
        this.patientIdNatio = patient.getId_nat(); 
        loadPatientDetailsFromDatabase();
    }




    // Gestionnaire d'événements pour le bouton "Add_button"
    @FXML
    void Add_button(ActionEvent event) {
        // Récupération des données entrées par l'utilisateur
        LocalDate selectedDate1 = date1_Input.getValue();
        LocalDate selectedDate2 = date2_Input.getValue();
        String remarque = rem_Input.getText().trim();
        // Vérification des champs requis
        if (selectedDate1 == null || selectedDate2 == null || remarque.isEmpty()) {
            // Affichage d'une alerte en cas de champs manquants
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Incomplete Fields");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all the required fields.");
            alert.showAndWait();
            return; 
        }
        // Création d'objets Date pour la base de données
        java.sql.Date date1_I = java.sql.Date.valueOf(selectedDate1);
        java.sql.Date date2_I = java.sql.Date.valueOf(selectedDate2);

        String patientIdNatio = patient.getId_nat(); 
        
        // Création et sauvegarde des détails du patient dans la base de données
        PatientDetails patientDetails = new PatientDetails(date1_I.toLocalDate(), remarque, date2_I.toLocalDate());
        savePatientDetailsToDatabase(patientDetails, patientIdNatio);

        detailsTableViews.add(patientDetails);
        clearInputFields();
    }




    // Méthode pour charger les détails d'un patient depuis la base de données
    private void loadPatientDetailsFromDatabase() {
        // Connexion à la base de données et requête de sélection des détails du patient
        detailsTableViews.clear(); 

        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            String selectQuery = "SELECT * FROM patient_details WHERE patient_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            preparedStatement.setString(1, patientIdNatio); 

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




    // Méthode pour sauvegarder les détails du patient dans la base de données
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


    // Méthode pour nettoyer les champs d'entrée
    private void clearInputFields() {
        rem_Input.clear();
        date1_Input.setValue(null);
        date2_Input.setValue(null);
    }

    


    @FXML
    private ImageView imageUploaderView;
    
    // Gestionnaire d'événements pour choisir une image à télécharger
    @FXML
    private void chooseImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");
        File selectedFile = fileChooser.showOpenDialog(imageUploaderView.getScene().getWindow());

        if (selectedFile != null) {
            // Création d'une image à partir du fichier sélectionné
            Image selectedImage = new Image(selectedFile.toURI().toString());
            // Affichage de l'image dans la vue
            imageUploaderView.setImage(selectedImage);
        }
    }


    
    // Gestionnaire d'événements pour télécharger une image
    @FXML
    private void uploadImage(ActionEvent event) {
        Image selectedImage = imageUploaderView.getImage();
        String newImageName = newImageNameField.getText().trim();

        if (selectedImage != null && !newImageName.isEmpty()) {
            try {
                // Sauvegarde de l'image dans la base de données
                saveImageToDatabase(selectedImage, newImageName);
                // Effacement de la liste des images et rafraîchissement
                clearImageListAndRefresh(); 
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Erreur de téléchargement d'image", "Une erreur s'est produite lors du téléchargement de l'image.");
            }
        } else {
            showAlert("Erreur de téléchargement d'image", "Veuillez sélectionner une image et entrer un nom.");
        }
    }

    // Méthode pour vider la liste des images et rafraîchir l'affichage
    private void clearImageListAndRefresh() {
        // Effacement de la liste d'images
        imageListView.getItems().clear();
        // Rechargement des images depuis la base de données
        loadImages();
    }
    

    // Méthode pour sauvegarder une image dans la base de données
    private void saveImageToDatabase(Image image, String imageName) throws IOException {
        // Requête d'insertion des données de l'image dans la base de données
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            String insertQuery = "INSERT INTO patient_images (patient_id_natio, image_name, image_data) VALUES (?, ?, ?)";
            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                // Remplissage des paramètres de la requête
                insertStatement.setString(1, patientIdNatio);
                insertStatement.setString(2, imageName);
                
                // Conversion de l'image JavaFX en BufferedImage
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                // Écriture de l'image dans le flux de sortie
                ImageIO.write(bufferedImage, "png", outputStream);
                InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
                insertStatement.setBinaryStream(3, inputStream);

                // Exécution de la requête d'insertion
                insertStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

   // Méthode pour obtenir les noms d'images depuis la base de données
    private List<String> getImageNamesFromDatabase() {
        List<String> imageNames = new ArrayList<>();

        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            // Requête pour sélectionner les noms d'images associés au patient
            String selectQuery = "SELECT image_name FROM patient_images WHERE patient_id_natio = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
            preparedStatement.setString(1, patientIdNatio);
            
            // Exécution de la requête et traitement des résultats
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

    // Méthode pour charger les images dans la liste
    private void loadImages() {
        // Obtention des noms d'images depuis la base de données
        List<String> imageNames = getImageNamesFromDatabase();

        // Effacement de la liste actuelle et ajout des nouveaux noms
        imageListView.getItems().clear();
        imageListView.getItems().addAll(imageNames);

        // Gestionnaire d'événements pour sélectionner une image de la liste
        imageListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Chargement de l'image sélectionnée depuis la base de données
                Image selectedImage = loadImageFromDatabase(newValue);
                if (selectedImage != null) {
                    // Affichage de l'image chargée
                    loadedImageView.setImage(selectedImage);
                } else {
                    loadedImageView.setImage(null); 
                    showAlert("Erreur de chargement d'image", "Impossible de charger l'image sélectionnée.");
                }
            } else {
                loadedImageView.setImage(null);
            }
        });
    }
    

    // Méthode pour charger une image depuis la base de données
    private Image loadImageFromDatabase(String imageName) {

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            // Requête pour récupérer les données de l'image
            String selectQuery = "SELECT image_data FROM patient_images WHERE patient_id_natio = ? AND image_name = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                // Remplissage des paramètres de la requête
                preparedStatement.setString(1, patientIdNatio);
                preparedStatement.setString(2, imageName);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        // Obtention des données binaires de l'image
                        InputStream inputStream = resultSet.getBinaryStream("image_data");
                        // Création et retour de l'image à partir des données binaires
                        return new Image(inputStream);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    
    // Méthode pour afficher une alerte
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}


