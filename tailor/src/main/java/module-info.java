module tailor {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;
    requires sewagelib;
    opens ite.kubak.app to javafx.graphics, javafx.fxml;
}