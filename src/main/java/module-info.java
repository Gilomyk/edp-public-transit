module com.example.projectedp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.media;

    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires java.net.http;
    requires com.google.gson;
    requires jdk.jsobject;

    opens com.example.projectedp to javafx.fxml;
    opens com.example.projectedp.controller to javafx.fxml;

    exports com.example.projectedp;
    exports com.example.projectedp.controller;
    exports com.example.projectedp.event;
    exports com.example.projectedp.model;
}