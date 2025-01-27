package ite.kubak.app;

import ite.kubak.model.TankerListener;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class TankerController {

    @FXML private TextField tanker_port;
    @FXML private TextField tanker_name;
    @FXML private TextField office_name;
    @FXML private TextField sewage_name;
    @FXML private TextField tailor_port;
    @FXML private TextField tailor_host;
    @FXML private TextField max_volume;
    @FXML private Button ok_button;
    @FXML private Button register_button;
    @FXML private Button ready_button;
    @FXML private Button goto_sewage_button;
    @FXML private ProgressBar progress;
    @FXML private Label percentage;
    @FXML private Label sewage;
    @FXML private Label house;
    private TankerListener listener = new TankerListener();


    @FXML
    public void ok_button_clicked(){
        try{
            int max_value = Integer.parseInt(max_volume.getText());
            int port = Integer.parseInt(tanker_port.getText());
            int port_tailor = Integer.parseInt(tailor_port.getText());
            String name_tanker = tanker_name.getText();
            String name_office = office_name.getText();
            String name_sewage = sewage_name.getText();
            String host_tailor = tailor_host.getText();
            if(max_value<=0) throw new IllegalArgumentException();
            if(port<0 || port>65535) throw new IllegalArgumentException();
            if(port_tailor<0 || port_tailor>65535) throw new IllegalArgumentException();
            if(!listener.testConnection(host_tailor,port_tailor,name_sewage,name_office)) throw new RuntimeException();
            listener.start(max_value,port,name_tanker,name_office,name_sewage,port_tailor,host_tailor);
            tanker_port.setDisable(true);
            max_volume.setDisable(true);
            tailor_port.setDisable(true);
            tanker_name.setDisable(true);
            office_name.setDisable(true);
            sewage_name.setDisable(true);
            tailor_host.setDisable(true);
            ok_button.setDisable(true);
            register_button.setDisable(false);
            startProgressBarUpdate();
        } catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("NIEPOPRAWNE DANE");
            alert.setHeaderText("Wprowadzono niepoprawne dane portu lub hosta");
            alert.setContentText("Sprawdź wprowadzone dane");
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
    public void goto_sewage_button_clicked(){
        sewage.setText("W drodze do oczyszczalni");
        new Thread(() -> {
            listener.use_sewage_plant();
            Platform.runLater(() -> {
                sewage.setText("");
            });
        }).start();
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
                    if(listener.getWay()) house.setText("W drodze na zlecenie.");
                    else house.setText("");
                } );
            }
        }).start();
    }

}
