module uk.co.jackoftrades {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;
    requires org.apache.logging.log4j;
    requires org.jetbrains.annotations;
    requires org.antlr.antlr4.runtime;
    requires java.desktop;
    requires javafx.media;
    requires com.sun.jna.platform;
    requires com.sun.jna;

    opens uk.co.jackoftrades to javafx.fxml;
    exports uk.co.jackoftrades;
}