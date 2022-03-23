package com.mjkrt.rendr.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class DataTable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "tableId")
    private long tableId;

    //primary key
    @OneToMany(mappedBy = "dataTable")
    private Set<DataHeader> dataHeaders;

    //foreign key
    @ManyToOne
    @JoinColumn(name="sheetId", nullable = false)
    private Sheet sheet;

    private long rowNum;
    private long colNum;

    public DataTable() {}

    public long getTableId() {
        return tableId;
    }


    public long getRowNum() {
        return rowNum;
    }

    public long getColNum() {
        return colNum;
    }

    public void setTableId(long tableId) {
        this.tableId = tableId;
    }

    public void setRowNum(long rowNum) {
        this.rowNum = rowNum;
    }

    public void setColNum(long colNum) {
        this.colNum = colNum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataTable dataTable = (DataTable) o;
        return tableId == dataTable.tableId && rowNum == dataTable.rowNum && colNum == dataTable.colNum;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableId, rowNum, colNum);
    }

    @Override
    public String toString() {
        return "DataTable{" +
                "tableId=" + tableId +
                ", rowNum=" + rowNum +
                ", colNum=" + colNum +
                '}';
    }
}
