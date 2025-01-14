package ite.kubak.app;

import ite.kubak.model.TankerListener;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class TankerController {

    @FXML private TextField tanker_port;
    @FXML private TextField tanker_host;
    @FXML private TextField office_port;
    @FXML private TextField sewage_port;
    @FXML private TextField sewage_host;
    @FXML private TextField max_volume;
    @FXML private Button ok_button;
    @FXML private Button register_button;
    @FXML private Button ready_button;
    @FXML private Button goto_sewage_button;
    @FXML private ProgressBar progress;
    @FXML private Label percentage;
    private TankerListener listener = new TankerListener();

    @FXML
    public void ok_button_clicked(){
        try{
            int max_value = Integer.parseInt(max_volume.getText());
            String host = tanker_host.getText();
            int port = Integer.parseInt(tanker_port.getText());
            String host_sewage = sewage_host.getText();
            int port_sewage = Integer.parseInt(sewage_port.getText());
            int port_office = Integer.parseInt(office_port.getText());
            if(port<0 || port>65535) throw new IllegalArgumentException();
            if(port_sewage<0 || port_sewage>65535) throw new IllegalArgumentException();
            if(port_office<0 || port_office>65535) throw new IllegalArgumentException();
            if(max_value<=0) throw new IllegalArgumentException();
            if(listener.start(max_value, host, port, host_sewage, port_sewage, port_office)){
                tanker_port.setDisable(true);
                tanker_host.setDisable(true);
                office_port.setDisable(true);
                sewage_host.setDisable(true);
                sewage_port.setDisable(true);
                max_volume.setDisable(true);
                ok_button.setDisable(true);
                register_button.setDisable(false);
                startProgressBarUpdate();
            }
            else throw new IllegalArgumentException();
        } catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("NIEPOPRAWNE DANE");
            alert.setHeaderText("Wprowadzono niepoprawne dane portu lub hosta");
            alert.setContentText("Upewnij się, że porty to liczba od 0 do 65535, oraz że istnieją" +
                    "Oczyszcalnia oraz Biuro o danym porcie. Pojemność cysterny musi być liczbą naturalną >0");
            alert.showAndWait();
        }
    }

    @FXML
    public void register_button_clicked(){
        listener.register();
        register_button.setDisable(true);
        ready_button.setDisable(false);
        goto_sewage_button.setDisable(false);
    }

    @FXML
    public void ready_button_clicked(){
        listener.set_readiness();
    }

    @FXML
    public void do_button_clicked(){
        listener.pump_out_house();
    }

    @FXML
    public void goto_sewage_button_clicked(){
        listener.use_sewage_plant();
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
                    percentage.setText(String.format("%.2f%%", listener.getProgress() * 100));
                    if(listener.getReady()){
                        goto_sewage_button.setDisable(true);
                        ready_button.setDisable(true);
                    }
                    else if(!listener.getReady()){
                        goto_sewage_button.setDisable(false);
                        ready_button.setDisable(false);
                    }
                } );
            }
        }).start();
    }

}
