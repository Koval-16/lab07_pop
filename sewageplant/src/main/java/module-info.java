module sewageplant {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;
    requires communication;
    requires sewagelib;
    opens ite.kubak.app to javafx.graphics, javafx.fxml;
}