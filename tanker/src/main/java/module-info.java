module tanker {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;
    requires communication;
    opens ite.kubak.app to javafx.graphics, javafx.fxml;
    requires sewagelib;
}