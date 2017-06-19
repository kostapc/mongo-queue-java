package ru.infon.queue.mongo;

import ru.infon.queuebox.RoutedMessage;

/**
 * 27.03.2017
 * @author KostaPC
 * Copyright (c) 2017 Infon. All rights reserved.
 */
public class JustPojoRouted implements RoutedMessage {

    private int intValue;
    private String stringValue;

    private String source;
    private String destination;

    public JustPojoRouted() {
    }

    public JustPojoRouted(int intValue, String stringValue) {
        this.intValue = intValue;
        this.stringValue = stringValue;
    }

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String getDestination() {
        return destination;
    }

    @Override
    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getIntValue() {
        return intValue;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    @Override
    public String toString() {
        return "JustPojo{" +
                "intValue=" + intValue +
                ", stringValue='" + stringValue + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JustPojoRouted)) return false;

        JustPojoRouted that = (JustPojoRouted) o;

        if (intValue != that.intValue) return false;
        if (!stringValue.equals(that.stringValue)) return false;
        if (!source.equals(that.source)) return false;
        return destination.equals(that.destination);
    }

    @Override
    public int hashCode() {
        int result = intValue;
        result = 31 * result + stringValue.hashCode();
        result = 31 * result + source.hashCode();
        result = 31 * result + destination.hashCode();
        return result;
    }
}
