package ite.kubak.app;

import ite.kubak.model.HouseListener;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

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
        listener.start(Integer.parseInt(port_field.getText()),
                host_field.getText(),
                Integer.parseInt(volume_field.getText()),
                officehost.getText(),
                Integer.parseInt(officeport.getText()));
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
                    percent.setText((listener.getProgress()*100)+"%");
                    if(listener.is_ordered()==1) order.setText("Zamówiono wywóz");
                    else order.setText("");
                } );
            }
        }).start();
    }

}
