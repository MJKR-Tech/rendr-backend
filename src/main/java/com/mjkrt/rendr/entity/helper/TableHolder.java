package com.mjkrt.rendr.entity.helper;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class TableHolder {
    public List<ColumnHeader> columnHeaders;
    //Could substitute with linkedlist / tree
    public Set<List<String>> dataRows;

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

    //todo sorting at the end
    //todo trimming
    //todo natural joining



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TableHolder that = (TableHolder) o;
        return Objects.equals(columnHeaders, that.columnHeaders) && Objects.equals(dataRows, that.dataRows);
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
