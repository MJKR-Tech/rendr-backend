package com.mjkrt.rendr.entity.helper;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class TableHolder {
    
    private List<ColumnHeader> columnHeaders;
    
    private Set<List<String>> dataRows;

    public TableHolder(List<ColumnHeader> columnHeaders, Set<List<String>> dataRows) {
        verifyStructure(columnHeaders, dataRows);
        this.columnHeaders = columnHeaders;
        this.dataRows = dataRows;
    }

    private static void verifyStructure(List<ColumnHeader> columnHeaders, Set<List<String>> dataRows) {
        int headerSize = columnHeaders.size();
        for (List<String> row : dataRows) {
            if (row.size() == headerSize) {
                continue;
            }
            throw new IllegalArgumentException("Header size and row size are not equal in table holder");
        }
    }

    public List<ColumnHeader> getColumnHeaders() {
        return columnHeaders;
    }

    public void setColumnHeaders(List<ColumnHeader> columnHeaders) {
        this.columnHeaders = columnHeaders;
    }

    public Set<List<String>> getDataRows() {
        return dataRows;
    }
    
    public void setDataRows(Set<List<String>> dataRows) {
        this.dataRows = dataRows;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TableHolder that = (TableHolder) o;
        return Objects.equals(columnHeaders, that.columnHeaders)
                && Objects.equals(dataRows, that.dataRows);
    }

    @Override
    public int hashCode() {
        return Objects.hash(columnHeaders, dataRows);
    }

    @Override
    public String toString() {
        return "TableHolder{" +
                "columnHeaders=" + columnHeaders +
                ", dataRows=" + dataRows +
                '}';
    }
}
