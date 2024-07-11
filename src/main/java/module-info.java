module com.accounting.app {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
//    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires com.zaxxer.hikari;

    opens com.accounting.app to javafx.fxml;
    exports com.accounting.app;
    exports com.accounting.app.controller;
    opens com.accounting.app.controller to javafx.fxml;
}