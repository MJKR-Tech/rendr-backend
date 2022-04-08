package com.mjkrt.rendr.entity;

import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * DataCell.
 * 
 * This instance represents an entry in data_cell SQL table.
 * It has a many-to-one relationship with DataSheet. 
 */
@Entity
public class DataCell {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long cellId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="sheetId", nullable = false)
    private DataSheet dataSheet;
    
    private int rowNum;
    
    private int colNum;
    
    private String field;

    public DataCell() {
    }

    public DataCell(long cellId, DataSheet dataSheet, int rowNum, int colNum, String field) {
        this.cellId = cellId;
        this.dataSheet = dataSheet;
        this.rowNum = rowNum;
        this.colNum = colNum;
        this.field = field;
    }

    public DataCell(String field, int rowNum, int colNum) {
        this.rowNum = rowNum;
        this.colNum = colNum;
        this.field = field;
    }

    public long getCellId() {
        return cellId;
    }

    public void setCellId(long cellId) {
        this.cellId = cellId;
    }

    public DataSheet getDataSheet() {
        return dataSheet;
    }

    public void setDataSheet(DataSheet dataSheet) {
        this.dataSheet = dataSheet;
    }

    public int getRowNum() {
        return rowNum;
    }

    public void setRowNum(int rowNum) {
        this.rowNum = rowNum;
    }

    public int getColNum() {
        return colNum;
    }

    public void setColNum(int colNum) {
        this.colNum = colNum;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataCell dataCell = (DataCell) o;
        return cellId == dataCell.cellId
                && rowNum == dataCell.rowNum
                && colNum == dataCell.colNum
                && Objects.equals(((dataSheet == null) ? null : dataSheet.getSheetId()),
                    ((dataCell.dataSheet == null) ? null : dataCell.dataSheet.getSheetId()))
                && Objects.equals(field, dataCell.field);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cellId,
                ((dataSheet == null) ? null : dataSheet.getSheetId()),
                rowNum,
                colNum,
                field);
    }

    @Override
    public String toString() {
        return "DataCell{" +
                "cellId=" + cellId +
                ", dataSheet=" + ((dataSheet == null) ? "" : dataSheet.getSheetId()) +
                ", rowNum=" + rowNum +
                ", colNum=" + colNum +
                ", field='" + field + '\'' +
                '}';
    }
}
