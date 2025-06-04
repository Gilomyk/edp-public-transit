module com.example.projectedp_19 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.example.projectedp_19 to javafx.fxml;
    exports com.example.projectedp_19;
    exports com.example.projectedp_19.controller;
    opens com.example.projectedp_19.controller to javafx.fxml;
}