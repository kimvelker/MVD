package com.trencadis.mvd;

/**
 * Created by Kimv on 4/7/2015.
 */
public class Sensor {

    public enum Type {READ, WRITE}

    private Type type;
    private String id, name;

    public Sensor(String id, String name, Type type){
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
