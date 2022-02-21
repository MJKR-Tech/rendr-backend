package com.mjkrt.rendr.entity;

public class ColumnHeader {

    public enum Types {
        STRING,
        DECIMAL,
        DATE
    }

    String name;
    Types type;
    String field;
    boolean isSelected;

    public ColumnHeader() {}

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Types getType() {
        return type;
    }

    public void setType(Types type) {
        this.type = type;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}