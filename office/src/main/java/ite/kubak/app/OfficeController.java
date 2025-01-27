package ite.kubak.app;

import interfaces.IHouse;
import interfaces.ITanker;
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

    @FXML private TextField office_port;
    @FXML private TextField office_name;
    @FXML private TextField sewage_name;
    @FXML private TextField tailor_port;
    @FXML private TextField tailor_host;
    @FXML private Button ok_button;
    @FXML private TableView<RegisteredInfo> tankers;
    @FXML private TableColumn<RegisteredInfo, Integer> numberColumn;
    @FXML private TableColumn<RegisteredInfo, String> nameColumn;
    @FXML private TableColumn<RegisteredInfo, ITanker> tankerColumn;
    @FXML private TableColumn<RegisteredInfo, Integer> readyColumn;
    @FXML private Button check_button;
    @FXML private Button pay_button;
    @FXML private Button givejob_button;
    @FXML private TableView<HouseInfo> orders;
    @FXML private TableColumn<HouseInfo, IHouse> orderHouse;
    @FXML private TableColumn<HouseInfo, String> orderName;
    private ObservableList<RegisteredInfo> tankersList = FXCollections.observableArrayList(); // Lista obserwowalna
    private ObservableList<HouseInfo> ordersList = FXCollections.observableArrayList();

    private OfficeListener listener = new OfficeListener();

    @FXML
    public void ok_button_clicked(){
        try{
            int port_office = Integer.parseInt(office_port.getText());
            String name_office = office_name.getText();
            String name_sewage = sewage_name.getText();
            int port_tailor = Integer.parseInt(tailor_port.getText());
            String name_tailor = tailor_host.getText();
            if(port_office<0 || port_office>65535) throw new IllegalArgumentException();
            if(port_tailor<0 || port_tailor>65535) throw new IllegalArgumentException();
            if(!listener.testConnection(name_tailor,port_tailor,name_sewage)) throw new RuntimeException();
            listener.start(port_office,name_office,name_sewage,port_tailor,name_tailor);
            update_table();
            pay_button.setDisable(false);
            check_button.setDisable(false);
            givejob_button.setDisable(false);
            office_port.setDisable(true);
            office_name.setDisable(true);
            sewage_name.setDisable(true);
            tailor_port.setDisable(true);
            tailor_host.setDisable(true);
            ok_button.setDisable(true);
        } catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("NIEPOPRAWNE DANE");
            alert.setHeaderText("Wprowadzono niepoprawne dane portu lub hosta");
            alert.setContentText("Sprawdź wprowadzone dane");
            alert.showAndWait();
        }

    }

    @FXML
    public void initialize() {
        numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("rmi_name"));
        tankerColumn.setCellValueFactory(new PropertyValueFactory<>("tanker"));
        readyColumn.setCellValueFactory(new PropertyValueFactory<>("ready"));
        tankers.setItems(tankersList);

        orderHouse.setCellValueFactory(new PropertyValueFactory<>("house"));
        orderName.setCellValueFactory(new PropertyValueFactory<>("rmi_name"));
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
            listener.set_job_to_tanker(selected_task.getHouse(),selected_tanker.getRmi_name());
        }
    }

}
