package ite.kubak.app;


import ite.kubak.model.SewagePlantListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class SewagePlantController {

    @FXML private TextField port;
    @FXML private Button button;
    private SewagePlantListener listener = new SewagePlantListener();

    @FXML
    public void button_clicked(){
        listener.start(Integer.parseInt(port.getText()));
        button.setDisable(true);
        port.setDisable(true);
    }

}
