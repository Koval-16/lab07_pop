package ite.kubak.app;


import ite.kubak.model.SewagePlantListener;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class SewagePlantController {

    @FXML private TextField port;
    @FXML private TextField host;
    @FXML private Button button;
    @FXML private TextField tailor_port;
    @FXML private TextField rmi_name;
    private SewagePlantListener listener = new SewagePlantListener();

    @FXML
    public void button_clicked(){
        try{
            int port_number = Integer.parseInt(port.getText());
            String host_sewage = host.getText();
            if(port_number<0 || port_number>65535) throw new IllegalArgumentException();
            int tailor_number = Integer.parseInt(tailor_port.getText());
            if(tailor_number<0 || tailor_number>65535) throw new IllegalArgumentException();
            String name = rmi_name.getText();
            if(!listener.testConnection(host_sewage,tailor_number)) throw new RuntimeException();
            listener.start(port_number,host_sewage,tailor_number,name);
            button.setDisable(true);
            port.setDisable(true);
            host.setDisable(true);
            tailor_port.setDisable(true);
            rmi_name.setDisable(true);
        } catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("NIEPOPRAWNY PORT");
            alert.setHeaderText("Wprowadzono niepoprawny port Oczyszczalni");
            alert.setContentText("Sprawdź wprowadzone dane");
            alert.showAndWait();
        }

    }

}
