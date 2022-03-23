package com.mjkrt.rendr.entity;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class DataHeader {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long headerId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="tableId", nullable = false)
    private DataTable dataTable;

    private String headerName;
    
    private long headerOrder;

    public DataHeader() {
    }

    public DataHeader(String headerName, long headerOrder) {
        this.headerName = headerName;
        this.headerOrder = headerOrder;
    }

    public DataTable getDataTable() {
        return dataTable;
    }

    public void setDataTable(DataTable dataTable) {
        this.dataTable = dataTable;
    }

    public void setHeaderId(long headerId) {
        this.headerId = headerId;
    }

    public long getHeaderId() {
        return headerId;
    }

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    public long getHeaderOrder() {
        return headerOrder;
    }

    public void setHeaderOrder(long headerOrder) {
        this.headerOrder = headerOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataHeader that = (DataHeader) o;
        return headerId == that.headerId
                && headerOrder == that.headerOrder
                && Objects.equals(dataTable.getTableId(), that.dataTable.getTableId())
                && Objects.equals(headerName, that.headerName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(headerId, dataTable.getTableId(), headerName, headerOrder);
    }

    @Override
    public String toString() {
        return "DataHeader{" +
                "headerId=" + headerId +
                ", dataTable=" + dataTable.getTableId() +
                ", headerName='" + headerName + '\'' +
                ", headerOrder=" + headerOrder +
                '}';
    }
}
