package ite.kubak.app;

import ite.kubak.model.TailorListener;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class TailorController {
    @FXML TextField port;
    @FXML TextField host;
    @FXML Button button;
    private TailorListener listener = new TailorListener();

    @FXML
    public void button_clicked(){
        try{
            int tailor_port = Integer.parseInt(port.getText());
            String tailor_host = host.getText();
            if(tailor_port<0 || tailor_port>65535) throw new IllegalArgumentException();
            listener.start(tailor_port,tailor_host);
            button.setDisable(true);
            port.setDisable(true);
            host.setDisable(true);
        } catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("NIEPOPRAWNE DANE");
            alert.setHeaderText("Wprowadzono niepoprawne dane portu");
            alert.setContentText("Upewnij się, że porty to liczba od 0 do 65535");
            alert.showAndWait();
        }

    }
}
