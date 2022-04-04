package com.mjkrt.rendr.entity;

import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mjkrt.rendr.entity.helper.DataDirection;

@Entity
public class DataContainer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long containerId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="tableId", nullable = false)
    private DataTable dataTable;
    
    @Column(columnDefinition = "ENUM('HORIZONTAL', 'VERTICAL')")
    @Enumerated(EnumType.STRING)
    private DataDirection direction = null;
    
    private String alias;

    private long rowNum;

    private long colNum;
    
    public DataContainer() {
    }

    public DataContainer(long containerId,
            DataTable dataTable,
            DataDirection direction,
            String alias,
            long rowNum,
            long colNum) {
        
        this.containerId = containerId;
        this.dataTable = dataTable;
        this.direction = direction;
        this.alias = alias;
        this.rowNum = rowNum;
        this.colNum = colNum;
    }

    public DataContainer(long containerId, DataDirection direction, String alias, long rowNum, long colNum) {
        this.containerId = containerId;
        this.direction = direction;
        this.alias = alias;
        this.rowNum = rowNum;
        this.colNum = colNum;
    }

    public DataContainer(DataDirection direction, String alias, long rowNum, long colNum) {
        this.direction = direction;
        this.alias = alias;
        this.rowNum = rowNum;
        this.colNum = colNum;
    }

    public long getContainerId() {
        return containerId;
    }

    public void setContainerId(long containerId) {
        this.containerId = containerId;
    }

    public DataTable getDataTable() {
        return dataTable;
    }

    public void setDataTable(DataTable dataTable) {
        this.dataTable = dataTable;
    }

    public DataDirection getDirection() {
        return direction;
    }

    public void setDirection(DataDirection direction) {
        this.direction = direction;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public long getRowNum() {
        return rowNum;
    }

    public void setRowNum(long rowNum) {
        this.rowNum = rowNum;
    }

    public long getColNum() {
        return colNum;
    }

    public void setColNum(long colNum) {
        this.colNum = colNum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataContainer that = (DataContainer) o;
        return containerId == that.containerId
                && rowNum == that.rowNum
                && colNum == that.colNum
                && Objects.equals(
                        (dataTable == null) ? null : dataTable.getTableId(),
                        (that.dataTable == null) ? null : that.dataTable.getTableId())
                && direction == that.direction
                && Objects.equals(alias, that.alias);
    }

    @Override
    public int hashCode() {
        return Objects.hash(containerId,
                (dataTable == null) ? null : dataTable.getTableId(),
                direction,
                alias,
                rowNum,
                colNum);
    }

    @Override
    public String toString() {
        return "DataContainer{" +
                "headerId=" + containerId +
                ", dataTable=" + ((dataTable == null) ? "" : dataTable.getTableId()) +
                ", direction=" + direction +
                ", containerAlias='" + alias + '\'' +
                ", rowNum=" + rowNum +
                ", colNum=" + colNum +
                '}';
    }
}
