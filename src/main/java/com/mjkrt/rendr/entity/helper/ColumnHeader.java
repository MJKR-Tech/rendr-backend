package com.mjkrt.rendr.entity.helper;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mjkrt.rendr.entity.DataDirection;

public class ColumnHeader implements Comparable<ColumnHeader> {

    public enum Types {
        @JsonProperty("string") STRING,
        @JsonProperty("decimal") DECIMAL,
        @JsonProperty("date") DATE,
        @JsonProperty("double") DOUBLE
    }

    String name;
    
    Types type;
    
    String field;

    DataDirection direction = DataDirection.HORIZONTAL;
    
    @JsonProperty(value="isSelected")
    boolean isSelected;

    public ColumnHeader() {
    }
    
    public ColumnHeader(String headerName) {
        this.name = headerName;
    }

    public DataDirection getDirection() {
        return direction;
    }

    public void setDirection(DataDirection direction) {
        this.direction = direction;
    }

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

    @Override
    public int compareTo(ColumnHeader that) {
        return this.getName().compareTo(that.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ColumnHeader that = (ColumnHeader) o;
        return isSelected == that.isSelected
                && Objects.equals(name, that.name)
                && type == that.type
                && Objects.equals(field, that.field);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, field, isSelected);
    }

    @Override
    public String toString() {
        return "ColumnHeader{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", field='" + field + '\'' +
                ", isSelected=" + isSelected +
                '}';
    }
}
