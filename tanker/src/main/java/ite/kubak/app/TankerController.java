package ite.kubak.app;

import ite.kubak.model.TankerListener;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

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
    private TankerListener listener = new TankerListener();

    @FXML
    public void ok_button_clicked(){
        int max_value = Integer.parseInt(max_volume.getText());
        String host = tanker_host.getText();
        int port = Integer.parseInt(tanker_port.getText());
        String host_sewage = sewage_host.getText();
        int port_sewage = Integer.parseInt(sewage_port.getText());
        int port_office = Integer.parseInt(office_port.getText());
        listener.start(max_value, host, port, host_sewage, port_sewage, port_office);
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
