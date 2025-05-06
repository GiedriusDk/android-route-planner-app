package com.example.myapplication;

public class ObjectItem {
    private String name;

    public ObjectItem(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


    @Override
    public String toString() {
        return name;
    }
}
