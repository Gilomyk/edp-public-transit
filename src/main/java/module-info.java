module com.example.projectedp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.example.projectedp to javafx.fxml;
    opens com.example.projectedp.controller to javafx.fxml;

    exports com.example.projectedp;
    exports com.example.projectedp.controller;
    exports com.example.projectedp.event;
    exports com.example.projectedp.model;
}