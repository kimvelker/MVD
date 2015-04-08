package com.trencadis.mvd.global;

import java.util.Calendar;

public class Entry {

    public static final String READ = "R";
    public static final String WRITE = "W";
    public static final String OK = "ok";
    public static final String ERROR = "error";

    public enum Type {READ, WRITE}

    private String lbbId, sensorId, valueTo, valueFrom, lastMessage, timestamp, timestampSent = "";

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

    public Entry(String lbbId, String sensorId, String type, String valueTo, String valueFrom, String lastMessage, String timestamp, String timestampSent){
        this.lbbId = lbbId;
        this.sensorId = sensorId;

        setType(type);

        this.valueFrom = valueFrom;

        this.valueTo = valueTo;

        this.lastMessage = lastMessage;

        this.timestamp = timestamp;

        this.timestampSent = timestampSent;

    }

    public boolean mustSend(){
        return (timestampSent.length() == 0);
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

        Calendar c = Calendar.getInstance();

        timestamp += c.get(Calendar.YEAR);

        int month = c.get(Calendar.MONTH);

        if(month < 10){
            timestamp += "-0" + month;
        }else{
            timestamp += "-" + month;
        }

        int day = c.get(Calendar.DAY_OF_MONTH);

        if(day < 10){
            timestamp += "-0" + day;
        }else{
            timestamp += "-" + day;
        }

        timestamp += " ";

        int hour = c.get(Calendar.HOUR_OF_DAY);

        if(hour < 10){
            timestamp += "-0" + hour;
        }else{
            timestamp += "-" + hour;
        }

        int minute = c.get(Calendar.MINUTE);

        if(minute < 10){
            timestamp += "-0" + minute;
        }else{
            timestamp += "-" + minute;
        }

        int seconds = c.get(Calendar.SECOND);

        if(seconds < 10){
            timestamp += "-0" + seconds;
        }else{
            timestamp += "-" + seconds;
        }

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
