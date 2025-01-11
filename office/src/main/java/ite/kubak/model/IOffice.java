package ite.kubak.model;

public interface IOffice {

    // Metoda pozwalająca cysternie zarejestrować się
    // Param: host - dane hosta, port - dane portu
    // Używa jej TANKER
    int register(String host, int port);

    // Metoda pozwalająca domowi zamówić wywóz nieczystości
    // Param: host - dane hosta, port - dane portu
    // Używa jej HOUSE
    int order(String host, int port);

    // Metoda pozwalająca cysternie zgłosić gotowość
    // Param: number - numer cysterny
    // Używa jej TANKER
    void setReadyToServe(int number);

}
