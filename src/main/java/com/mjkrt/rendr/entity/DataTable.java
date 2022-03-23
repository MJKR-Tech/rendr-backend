package com.mjkrt.rendr.entity;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

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
    private DataSheet dataSheet;

    private long rowNum;
    private long colNum;

    public DataTable(long rowNum, long colNum) {
        this.rowNum = rowNum;
        this.colNum = colNum;
    }

    public void setDataHeader(List<DataHeader> dataHeader) {
        this.dataHeader = dataHeader;
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
    public String toString() {
        return "DataTable{" +
                "tableId=" + tableId +
                ", dataHeader=" + dataHeader +
                ", sheet=" + dataSheet.getSheetId() +
                ", rowNum=" + rowNum +
                ", colNum=" + colNum +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataTable dataTable = (DataTable) o;
        return tableId == dataTable.tableId && rowNum == dataTable.rowNum && colNum == dataTable.colNum && Objects.equals(dataHeader, dataTable.dataHeader) && Objects.equals(dataSheet.getSheetId(), dataTable.dataSheet.getSheetId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableId, dataHeader, dataSheet.getSheetId(), rowNum, colNum);
    }
}
