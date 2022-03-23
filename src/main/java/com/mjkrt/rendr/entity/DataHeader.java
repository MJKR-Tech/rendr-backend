package com.mjkrt.rendr.entity;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class DataHeader {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long headerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="tableId", nullable = false)
    private DataTable dataTable;

    private String headerName;
    private long headerOrder;

    public DataHeader() {}

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
    public String toString() {
        return "DataHeader{" +
                "headerId=" + headerId +
                ", dataTable=" + dataTable.getTableId() +
                ", headerName='" + headerName + '\'' +
                ", headerOrder=" + headerOrder +
                '}';
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
}
