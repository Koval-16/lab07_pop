package ite.kubak.app;

import ite.kubak.model.HouseListener;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class HouseController {

    @FXML private Button port_button;
    @FXML private Button use_button;
    @FXML private Button office_button;
    private HouseListener listener = new HouseListener();
    @FXML private TextField port_field;
    @FXML private TextField host_field;
    @FXML private TextField volume_field;
    @FXML private TextField officehost;
    @FXML private TextField officeport;
    @FXML private ProgressBar progress;
    @FXML private Label percent;
    @FXML private Label host;
    @FXML private Label port;
    @FXML private Label order;

    @FXML
    public void port_button_clicked(){
        try{
            int port_val = Integer.parseInt(port_field.getText());
            String host_val = host_field.getText();
            int volume = Integer.parseInt(volume_field.getText());
            String office_host = officehost.getText();
            int office_port = Integer.parseInt(officeport.getText());
            if(port_val<0 || port_val>65535) throw new IllegalArgumentException();
            if(office_port<0 || office_port>65535) throw new IllegalArgumentException();
            if(volume<=0) throw new IllegalArgumentException();
            if(listener.start(port_val,host_val,volume,office_host,office_port)){
                startProgressBarUpdate();
                port_button.setDisable(true);
                use_button.setDisable(false);
                office_button.setDisable(false);
                port_field.setDisable(true);
                host_field.setDisable(true);
                volume_field.setDisable(true);
                host.setText("Host biura: "+officehost.getText());
                port.setText("Port biura: "+officeport.getText());
            }
            else throw new IllegalArgumentException();
        } catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("NIEPOPRAWNE DANE");
            alert.setHeaderText("Wprowadzono niepoprawne dane portu lub hosta");
            alert.setContentText("Upewnij się, że porty to liczba od 0 do 65535, oraz że istnieje podany port biura." +
                    " Zwróć też uwagę, że pojemność szamba musi być liczbą naturalną >0");
            alert.showAndWait();
        }
    }

    @FXML
    public void office_button_clicked(){
        listener.set_office_address(officehost.getText(),Integer.parseInt(officeport.getText()));
    }

    @FXML
    public void usage_button_clicked(){
        listener.switch_usage();
    }

    private void startProgressBarUpdate() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(() ->{
                    progress.setProgress(listener.getProgress());
                    percent.setText(String.format("%.2f%%", listener.getProgress() * 100));
                    if(listener.is_ordered()==1) order.setText("Zamówiono wywóz");
                    else order.setText("");
                } );
            }
        }).start();
    }

}
