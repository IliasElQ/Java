module com.example.javfx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    requires java.sql;
    requires java.desktop;
    requires javafx.swing;

    opens com.example.javfx to javafx.fxml;
    exports com.example.javfx;
}