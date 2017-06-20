package ru.infon.queuebox;

/**
 * 07.06.2017
 * @author KostaPC
 * 2017 Infon ZED
 **/
public interface RoutedMessage {

    String getSource();
    String getDestination();
    void setSource(String source);
    void setDestination(String destination);

}
