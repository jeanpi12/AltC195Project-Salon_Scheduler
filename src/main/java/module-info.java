module com.example.altc195project {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.altc195project.main       to javafx.graphics, javafx.fxml;
    opens com.example.altc195project.controller to javafx.fxml;
    opens com.example.altc195project.DAO        to javafx.fxml;
    opens com.example.altc195project.models     to javafx.fxml, javafx.base;

    exports com.example.altc195project.database;
}
