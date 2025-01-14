package ite.kubak.app;


import ite.kubak.model.SewagePlantListener;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class SewagePlantController {

    @FXML private TextField port;
    @FXML private Button button;
    private SewagePlantListener listener = new SewagePlantListener();

    @FXML
    public void button_clicked(){
        try{
            int port_number = Integer.parseInt(port.getText());
            if(port_number<0 || port_number>65535) throw new IllegalArgumentException();
            listener.start(Integer.parseInt(port.getText()));
            button.setDisable(true);
            port.setDisable(true);
        } catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("NIEPOPRAWNY PORT");
            alert.setHeaderText("Wprowadzono niepoprawny port Oczyszczalni");
            alert.setContentText("Wprowadź liczbę od 0 do 65535");
            alert.showAndWait();
        }

    }

}
