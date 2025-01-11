package ite.kubak.model;

public interface ISewagePlant {

    // Metoda, którą cysterna wyrzuca nieczystości
    // Param: number - numer cysterny, volume - objetosc nieczystosci
    // Używa jej TANKER
    int setPumpIn(int number, int volume);

    // Metoda, którą biuro może sprawdzić ile dana cysterna przywiozła objętości
    // Param: number - numer cysterny
    // Używa jej OFFICE
    int getStatus(int number);

    // Metoda, którą biuro rozlicza się z cysterną i zeruje jej licznik
    // Param: number - numer cysterny
    // Uzywa jej OFFICE
    void setPayoff(int number);

}
