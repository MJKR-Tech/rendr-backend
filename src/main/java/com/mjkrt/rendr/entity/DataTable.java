package com.mjkrt.rendr.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class DataTable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long tableId;

    //primary key
    @OneToMany(mappedBy = "dataTable", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<DataHeader> dataHeader = new ArrayList<>();

    //foreign key
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="sheetId", nullable = false)
    private DataSheet dataSheet;

    private long rowNum;
    
    private long colNum;

    public DataTable() {
    }

    public DataTable(long rowNum, long colNum) {
        this.rowNum = rowNum;
        this.colNum = colNum;
    }

    public void setDataHeader(List<DataHeader> dataHeader) {
        this.dataHeader.clear();
        this.dataHeader.addAll(dataHeader);
        dataHeader.forEach(header -> header.setDataTable(this));
    }

    public DataSheet getDataSheet() {
        return dataSheet;
    }

    public void setDataSheet(DataSheet dataSheet) {
        this.dataSheet = dataSheet;
    }

    public List<DataHeader> getDataHeader() {
        return dataHeader;
    }

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
        return tableId == dataTable.tableId
                && rowNum == dataTable.rowNum
                && colNum == dataTable.colNum
                && Objects.equals(dataHeader, dataTable.dataHeader)
                && Objects.equals(dataSheet.getSheetId(), dataTable.dataSheet.getSheetId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableId, dataHeader, dataSheet.getSheetId(), rowNum, colNum);
    }

    @Override
    public String toString() {
        return "DataTable{" +
                "tableId=" + tableId +
                ", dataHeader=" + dataHeader +
                ", sheet=" + dataSheet.getSheetId() +
                ", rowNum=" + rowNum +
                ", colNum=" + colNum +
                '}';
    }
}