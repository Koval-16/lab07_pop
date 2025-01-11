package ite.kubak.model;

public interface ITanker {

    // Metoda pozwalająca biurowi zlecenie wywozu nieczystości
    // Param: host - dane hosta, port - dane domu
    // Używa jej OFFICE
    void setJob(String host, int port);

}
