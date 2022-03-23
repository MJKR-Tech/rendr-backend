package com.mjkrt.rendr.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "DataTable")
public class DataTable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "tableId")
    private long tableId;

    //primary key
    @OneToMany(mappedBy = "dataTable", fetch = FetchType.LAZY)
    private List<DataHeader> dataHeader;

    //foreign key
    @ManyToOne(fetch = FetchType.LAZY)
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
    public String toString() {
        return "DataTable{" +
                "tableId=" + tableId +
                ", dataHeader=" + dataHeader +
                ", sheet=" + sheet.getSheetId() +
                ", rowNum=" + rowNum +
                ", colNum=" + colNum +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataTable dataTable = (DataTable) o;
        return tableId == dataTable.tableId && rowNum == dataTable.rowNum && colNum == dataTable.colNum && Objects.equals(dataHeader, dataTable.dataHeader) && Objects.equals(sheet.getSheetId(), dataTable.sheet.getSheetId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableId, dataHeader, sheet.getSheetId(), rowNum, colNum);
    }
}
