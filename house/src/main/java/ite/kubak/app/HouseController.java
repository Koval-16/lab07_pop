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
    @FXML private TextField name_field;
    @FXML private TextField volume_field;
    @FXML private TextField office_name;
    @FXML private TextField tailor_port;
    @FXML private TextField tailor_host;
    @FXML private ProgressBar progress;
    @FXML private Label percent;
    @FXML private Label order;

    @FXML
    public void port_button_clicked(){
        try{
            int port_val = Integer.parseInt(port_field.getText());
            String name = name_field.getText();
            int volume = Integer.parseInt(volume_field.getText());
            String name_office = office_name.getText();
            int port_tailor = Integer.parseInt(tailor_port.getText());
            String host_tailor = tailor_host.getText();
            if(volume<=0) throw new IllegalArgumentException();
            if(port_val<0 || port_val>65535) throw new IllegalArgumentException();
            if(port_tailor<0 || port_tailor>65535) throw new IllegalArgumentException();
            if(listener.testConnection(host_tailor,port_tailor,name_office)) throw new RuntimeException();
            listener.start(port_val,name,volume,name_office,port_tailor,host_tailor);
            startProgressBarUpdate();
            port_button.setDisable(true);
            use_button.setDisable(false);
            office_button.setDisable(false);
            name_field.setDisable(true);
            office_name.setDisable(true);
            tailor_port.setDisable(true);
            tailor_host.setDisable(true);
            port_field.setDisable(true);
            volume_field.setDisable(true);
        } catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("NIEPOPRAWNE DANE");
            alert.setHeaderText("Wprowadzono niepoprawne dane portu lub hosta");
            alert.setContentText("Sprawdź wprowadzone dane");
            alert.showAndWait();
        }
    }

    @FXML
    public void office_button_clicked(){
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
