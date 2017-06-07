package ru.infon.queue.mongo;

/**
 * 27.03.2017
 * @author KostaPC
 * Copyright (c) 2017 Infon. All rights reserved.
 */
public class JustPojo {

    private int intValue;
    private String stringValue;

    public JustPojo() {
    }

    public JustPojo(int intValue, String stringValue) {
        this.intValue = intValue;
        this.stringValue = stringValue;
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
        if (!(o instanceof JustPojo)) return false;

        JustPojo justPojo = (JustPojo) o;

        if (intValue != justPojo.intValue) return false;
        return stringValue != null ? stringValue.equals(justPojo.stringValue) : justPojo.stringValue == null;
    }

    @Override
    public int hashCode() {
        int result = intValue;
        result = 31 * result + (stringValue != null ? stringValue.hashCode() : 0);
        return result;
    }
}
