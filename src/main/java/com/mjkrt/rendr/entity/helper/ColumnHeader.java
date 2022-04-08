package com.mjkrt.rendr.entity.helper;

import java.util.Objects;

/**
 * ColumnHeader.
 *
 * This class represents a column's header in a table
 */
public class ColumnHeader implements Comparable<ColumnHeader> {
    
    public static ColumnHeader getMockColumnHeader() {
        return new ColumnHeader();
    }
    
    private String name = "";
    
    private ColumnDataType type = ColumnDataType.MOCK;
    
    private String field = "";
    
    private DataDirection appendDirection = DataDirection.VERTICAL;

    public ColumnHeader() {
    }

    public ColumnHeader(String name, ColumnDataType type, String field) {
        this.name = name;
        this.type = type;
        this.field = field;
    }

    public ColumnHeader(String name, ColumnDataType type) {
        this.name = name;
        this.type = type;
    }

    public ColumnHeader(String name, DataDirection appendDirection) {
        this.name = name;
        this.appendDirection = appendDirection;
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

    public DataDirection getAppendDirection() {
        return appendDirection;
    }

    public void setAppendDirection(DataDirection appendDirection) {
        this.appendDirection = appendDirection;
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
                && Objects.equals(field, that.field)
                && appendDirection == that.appendDirection;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type, field, appendDirection);
    }

    @Override
    public String toString() {
        return "ColumnHeader{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", field='" + field + '\'' +
                ", appendDirection=" + appendDirection +
                '}';
    }
}
