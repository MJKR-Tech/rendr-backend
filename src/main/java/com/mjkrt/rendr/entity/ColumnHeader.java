package com.mjkrt.rendr.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ColumnHeader {

    public enum Types {
        @JsonProperty("string") STRING,
        @JsonProperty("decimal") DECIMAL,
        @JsonProperty("date") DATE,
        @JsonProperty("double") DOUBLE
    }

    String name;
    Types type;
    String field;
    @JsonProperty(value="isSelected") boolean isSelected;

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
