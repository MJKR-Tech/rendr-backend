package com.mjkrt.rendr.entity.helper;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ColumnHeader implements Comparable<ColumnHeader> {

    public enum ColumnDataType {
        @JsonProperty("string") STRING,
        @JsonProperty("decimal") DECIMAL,
        @JsonProperty("date") DATE,
        @JsonProperty("double") DOUBLE
    }
    
    private String name;

    private ColumnDataType type;

    private String field;
    
    public ColumnHeader() {
    }

    public ColumnHeader(String name, ColumnDataType type, String field) {
        this.name = name;
        this.type = type;
        this.field = field;
    }

    public ColumnHeader(String headerName) {
        this.name = headerName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ColumnDataType getType() {
        return type;
    }

    public void setType(ColumnDataType type) {
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
        return this.getName()
                .compareTo(that.getName());
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
        return Objects.equals(name, that.name)
                && type == that.type
                && Objects.equals(field, that.field);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, field);
    }

    @Override
    public String toString() {
        return "ColumnHeader{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", field='" + field + '\'' +
                '}';
    }
}
