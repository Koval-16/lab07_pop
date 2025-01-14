package ite.kubak.app;

import ite.kubak.model.HouseInfo;
import ite.kubak.model.OfficeListener;
import ite.kubak.model.RegisteredInfo;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class OfficeController {

    @FXML private TextField office_field;
    @FXML private TextField host_field;
    @FXML private TextField port_field;
    @FXML private Button ok_button;
    @FXML private TableView<RegisteredInfo> tankers;
    @FXML private TableColumn<RegisteredInfo, Integer> numberColumn;
    @FXML private TableColumn<RegisteredInfo, String> hostColumn;
    @FXML private TableColumn<RegisteredInfo, Integer> portColumn;
    @FXML private TableColumn<RegisteredInfo, Integer> readyColumn;
    @FXML private Button check_button;
    @FXML private Button pay_button;
    @FXML private Button givejob_button;
    @FXML private TableView<HouseInfo> orders;
    @FXML private TableColumn<HouseInfo, String> orderHost;
    @FXML private TableColumn<HouseInfo, Integer> orderPort;
    private ObservableList<RegisteredInfo> tankersList = FXCollections.observableArrayList(); // Lista obserwowalna
    private ObservableList<HouseInfo> ordersList = FXCollections.observableArrayList();

    private OfficeListener listener = new OfficeListener();

    @FXML
    public void ok_button_clicked(){
        String sewage_host = host_field.getText();
        try{
            int sewage_port = Integer.parseInt(port_field.getText());
            int port = Integer.parseInt(office_field.getText());
            if(port<0 || port>65535) throw new IllegalArgumentException();
            if(sewage_port<0 || sewage_port>65535) throw new IllegalArgumentException();
            if(listener.start(sewage_host, sewage_port, port)){
                update_table();
                pay_button.setDisable(false);
                check_button.setDisable(false);
                givejob_button.setDisable(false);
                ok_button.setDisable(true);
                office_field.setDisable(true);
                host_field.setDisable(true);
                port_field.setDisable(true);
            }
            else throw new IllegalArgumentException();
        } catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("NIEPOPRAWNE DANE");
            alert.setHeaderText("Wprowadzono niepoprawne dane portu lub hosta");
            alert.setContentText("Upewnij się, że porty to liczba od 0 do 65535, oraz że istnieje Oczyszczalnia o danym porcie");
            alert.showAndWait();
        }

    }

    @FXML
    public void initialize() {
        numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        hostColumn.setCellValueFactory(new PropertyValueFactory<>("host"));
        portColumn.setCellValueFactory(new PropertyValueFactory<>("port"));
        readyColumn.setCellValueFactory(new PropertyValueFactory<>("ready"));
        tankers.setItems(tankersList);

        orderHost.setCellValueFactory(new PropertyValueFactory<>("host"));
        orderPort.setCellValueFactory(new PropertyValueFactory<>("port"));
        orders.setItems(ordersList);
    }

    private void update_table(){
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Platform.runLater(() -> {
                    tankersList.setAll(listener.get_tankers_to_table());
                    ordersList.setAll(listener.get_orders());
                    initialize();
                });
            }
        }).start();
    }

    @FXML
    private void check_button_clicked(){
        RegisteredInfo selected = tankers.getSelectionModel().getSelectedItem();
        if(selected != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Informacja");
            alert.setHeaderText("Cysterna nr."+selected.getNumber()+" przewiozła "+listener.get_tanker_status(selected.getNumber())+"L zanieczyszczen");
            alert.setContentText("Cysterna nr."+selected.getNumber()+" przewiozła "+listener.get_tanker_status(selected.getNumber())+"L zanieczyszczen");
            alert.showAndWait();
        }
    }

    @FXML
    private void pay_button_clicked(){
        RegisteredInfo selected = tankers.getSelectionModel().getSelectedItem();
        if(selected != null) {
            listener.pay_to_tanker(selected.getNumber());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Rozliczenie");
            alert.setHeaderText("Cysterna nr."+selected.getNumber()+" została rozliczona.");
            alert.setContentText("Cysterna nr."+selected.getNumber()+" została rozliczona.");
            alert.showAndWait();
        }
    }

    @FXML
    private void givejob_button_clicked(){
        RegisteredInfo selected_tanker = tankers.getSelectionModel().getSelectedItem();
        HouseInfo selected_task = orders.getSelectionModel().getSelectedItem();
        if(selected_task!=null && selected_tanker!=null && selected_tanker.getReady()==1){
            listener.set_job_to_tanker(selected_task.getHost(),selected_task.getPort(),selected_tanker.getHost(),selected_tanker.getPort());
        }
    }

}
