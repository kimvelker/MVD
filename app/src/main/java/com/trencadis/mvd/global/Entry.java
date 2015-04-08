package com.trencadis.mvd.global;

public class Entry {

    private static final String READ = "R";
    private static final String WRITE = "W";

    public enum Type {READ, WRITE}

    private String lbbId, sensorId, valueTo, valueFrom, lastMessage, timestamp;

    private Type type;

    public Entry(String lbbId, String sensorId, String type, String value, String lastMessage){
        this.lbbId = lbbId;
        this.sensorId = sensorId;

        setType(type);

        switch (type){
            case READ:
                valueFrom = value;
                valueTo = "";
                break;
            case WRITE:
                valueFrom = "";
                valueTo = value;
                break;
            default:
                break;
        }

        this.lastMessage = lastMessage;

        setTimestamp();

    }

    public Entry(String lbbId, String sensorId, String type, String valueTo, String valueFrom, String lastMessage, String timestamp){
        this.lbbId = lbbId;
        this.sensorId = sensorId;

        setType(type);

        this.valueFrom = valueFrom;

        this.valueTo = valueTo;

        this.lastMessage = lastMessage;

        this.timestamp = timestamp;

    }

    private void setType(String type) {
        switch (type){
            case READ:
                this.type = Type.READ;
                break;
            case WRITE:
                this.type = Type.WRITE;
                break;
            default:
                break;
        }
    }

    private void setTimestamp() {
        timestamp = "";

        timestamp = "" + (System.currentTimeMillis() / 1000);

    }

    public String getLbbId() {
        return lbbId;
    }

    public String getSensorId() {
        return sensorId;
    }

    public String getValueTo() {
        return valueTo;
    }

    public String getValueFrom() {
        return valueFrom;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getType() {
        switch (type){
            case READ:
                return READ;
            case WRITE:
                return WRITE;
            default:
                return "";
        }
    }
}
