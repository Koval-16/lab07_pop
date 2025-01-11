package ite.kubak.app;

import ite.kubak.model.HouseListener;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

public class HouseController {

    @FXML private Button port_button;
    @FXML private Button host_button;
    private HouseListener listener = new HouseListener();
    @FXML private TextField port_field;
    @FXML private TextField host_field;
    @FXML private TextField volume_field;
    @FXML private ProgressBar progress;

    @FXML
    public void port_button_clicked(){
        listener.start(Integer.parseInt(port_field.getText()),host_field.getText(),Integer.parseInt(volume_field.getText()));
        startProgressBarUpdate();
    }

    @FXML
    public void host_button_clicked(){
    }

    @FXML
    public void usage_button_clicked(){
        listener.switch_usage();
    }

    private void startProgressBarUpdate() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(500); // Odświeżaj co 500 ms
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Aktualizuj ProgressBar w wątku GUI
                Platform.runLater(() -> progress.setProgress(listener.getProgress()));
            }
        }).start();
    }

}
