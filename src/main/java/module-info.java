module com.example.sphsimulator {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.sphsimulator to javafx.fxml;
    exports com.example.sphsimulator;
}