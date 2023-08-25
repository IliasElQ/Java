package com.example.javfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;

public class CalendarController implements Initializable {

    // Dates de mise au point et d'aujourd'hui
    ZonedDateTime dateFocus;
    ZonedDateTime today;

    // Éléments du fichier FXML
    @FXML
    private Text year;

    @FXML
    private Text month;

    @FXML
    private FlowPane calendar;

    // Paramètres de connexion JDBC
    private String jdbcUrl = "jdbc:mysql://localhost:3306/patient_details";
    private String username = "root";
    private String password = "myroot2468";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Initialisation des dates
        dateFocus = ZonedDateTime.now();
        today = ZonedDateTime.now();

        // Dessin du calendrier
        drawCalendar();
    }

     // Action : Mois précédent
    @FXML
    void backOneMonth(ActionEvent event) {
        dateFocus = dateFocus.minusMonths(1);
        calendar.getChildren().clear();
        drawCalendar();
    }

     // Action : Mois suivant
    @FXML
    void forwardOneMonth(ActionEvent event) {
        dateFocus = dateFocus.plusMonths(1);
        calendar.getChildren().clear();
        drawCalendar();
    }

    // Méthode pour dessiner le calendrier
    private void drawCalendar() {
    // Met à jour l'affichage de l'année et du mois
    year.setText(String.valueOf(dateFocus.getYear()));
    month.setText(String.valueOf(dateFocus.getMonth()));

    // Obtient les dimensions du calendrier et les paramètres d'espacement
    double calendarWidth = calendar.getPrefWidth();
    double calendarHeight = calendar.getPrefHeight();
    double strokeWidth = 1;
    double spacingH = calendar.getHgap();
    double spacingV = calendar.getVgap();

    // Crée une carte pour stocker les détails du calendrier
    Map<Integer, List<PatientDetails>> calendarDetailsMap = createCalendarMap(getCalendarActivitiesMonth(dateFocus));

    // Détermine le nombre maximal de jours dans le mois en cours
    int monthMaxDate = dateFocus.getMonth().maxLength();
    if (dateFocus.getYear() % 4 != 0 && monthMaxDate == 29) {
        monthMaxDate = 28;
    }
    // Calcule le décalage du jour de la semaine pour le premier jour du mois
    int dateOffset = ZonedDateTime.of(dateFocus.getYear(), dateFocus.getMonthValue(), 1, 0, 0, 0, 0, dateFocus.getZone()).getDayOfWeek().getValue();

    // Boucle pour dessiner les cellules du calendrier
    for (int i = 0; i < 6; i++) {
        for (int j = 0; j < 7; j++) {
            StackPane stackPane = new StackPane();
            Rectangle rectangle = new Rectangle();
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setStroke(Color.BLACK);
            rectangle.setStrokeWidth(strokeWidth);
            double rectangleWidth = (calendarWidth / 7) - strokeWidth - spacingH;
            rectangle.setWidth(rectangleWidth);
            double rectangleHeight = (calendarHeight / 6) - strokeWidth - spacingV;
            rectangle.setHeight(rectangleHeight);
            stackPane.getChildren().add(rectangle);

            // Calcule la date affichée dans cette cellule
            int calculatedDate = (j + 1) + (7 * i);
            if (calculatedDate > dateOffset) {
                int currentDate = calculatedDate - dateOffset;
                if (currentDate <= monthMaxDate) {
                    // Affiche le numéro du jour dans la cellule
                    Text date = new Text(String.valueOf(currentDate));
                    double textTranslationY = -(rectangleHeight / 2) * 0.75;
                    date.setTranslateY(textTranslationY);
                    stackPane.getChildren().add(date);

                    // Récupère les détails des patients pour cette date
                    List<PatientDetails> patientDetailsList = calendarDetailsMap.get(currentDate);
                    if (patientDetailsList != null) {
                        createCalendarActivity(patientDetailsList, rectangleHeight, rectangleWidth, stackPane);
                    }
                }

                // Met en évidence le jour actuel
                if (today.getYear() == dateFocus.getYear() && today.getMonth() == dateFocus.getMonth() && today.getDayOfMonth() == currentDate) {
                    rectangle.setStroke(Color.BLUE);
                }
            }
            // Ajoute la cellule au calendrier
            calendar.getChildren().add(stackPane);
        }
    }
}

    // Méthode pour créer une activité du calendrier
    private void createCalendarActivity(List<PatientDetails> patientDetailsList, double rectangleHeight, double rectangleWidth, StackPane stackPane) {
    // Crée un conteneur VBox pour les activités du calendrier
    VBox calendarActivityBox = new VBox();

    // Parcours de la liste des détails des patients pour cette journée
    for (int k = 0; k < patientDetailsList.size(); k++) {
        // Limite le nombre d'activités affichées à 2, affiche "..." s'il y en a plus
        if (k >= 2) {
            Text moreActivities = new Text("...");
            calendarActivityBox.getChildren().add(moreActivities);
            // Gestionnaire d'événement pour afficher tous les détails si cliqué
            moreActivities.setOnMouseClicked(mouseEvent -> {
                System.out.println(patientDetailsList);
            });
            break;
        }
        // Crée un Text avec les détails du patient (remarque et date)
        Text text = new Text(patientDetailsList.get(k).getRemarque() + ", " + patientDetailsList.get(k).getDate2());
        calendarActivityBox.getChildren().add(text);
        // Gestionnaire d'événement pour afficher le détail complet si cliqué
        text.setOnMouseClicked(mouseEvent -> {
            System.out.println(text.getText());
        });
    }

    // Positionne verticalement la boîte d'activités à l'intérieur de la cellule du calendrier
    calendarActivityBox.setTranslateY((rectangleHeight / 2) * 0.20);
    // Définit les dimensions maximales de la boîte d'activités
    calendarActivityBox.setMaxWidth(rectangleWidth * 0.8);
    calendarActivityBox.setMaxHeight(rectangleHeight * 0.65);
    // Applique un style à la boîte d'activités
    calendarActivityBox.setStyle("-fx-background-color:GRAY");
    
    // Ajoute la boîte d'activités au StackPane parent
    stackPane.getChildren().add(calendarActivityBox);
}

    // Méthode pour créer une carte des détails du calendrier
    private Map<Integer, List<PatientDetails>> createCalendarMap(List<PatientDetails> patientDetailsList) {
    // Crée une carte pour stocker les détails du calendrier
    Map<Integer, List<PatientDetails>> calendarDetailsMap = new HashMap<>();

    // Parcours de la liste des détails des patients
    for (PatientDetails details : patientDetailsList) {
        // Obtient le jour du mois pour les détails du patient
        int dayOfMonth = details.getDate2().getDayOfMonth();
        // Associe les détails du patient au jour correspondant dans la carte
        calendarDetailsMap.computeIfAbsent(dayOfMonth, k -> new ArrayList<>()).add(details);
    }

    // Retourne la carte des détails du calendrier
    return calendarDetailsMap;
}

    // Méthode pour obtenir les activités du calendrier pour le mois donné
    private List<PatientDetails> getCalendarActivitiesMonth(ZonedDateTime dateFocus) {
        List<PatientDetails> patientDetailsList = new ArrayList<>();
        int year = dateFocus.getYear();
        int month = dateFocus.getMonthValue();

        try {
            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            String query = "SELECT date2, remarque FROM patient_details WHERE YEAR(date2) = ? AND MONTH(date2) = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, year);
                statement.setInt(2, month);

                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    LocalDate date2 = resultSet.getDate("date2").toLocalDate();
                    String remarque = resultSet.getString("remarque");
                    patientDetailsList.add(new PatientDetails(date2, remarque));
                }
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return patientDetailsList;
    }

    // Méthode pour changer de scène vers Scene1.fxml
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
