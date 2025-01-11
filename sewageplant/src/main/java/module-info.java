module sewageplant {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;
    opens ite.kubak.app to javafx.graphics, javafx.fxml;
}