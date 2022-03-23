package com.mjkrt.rendr.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Entity
public class DataHeader {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "headerId")
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataHeader that = (DataHeader) o;
        return headerId == that.headerId && headerOrder == that.headerOrder && Objects.equals(headerName, that.headerName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(headerId, headerName, headerOrder);
    }

    @Override
    public String toString() {
        return "DataHeader{" +
                "headerId=" + headerId +
                ", headerName='" + headerName +
                ", headerOrder=" + headerOrder +
                '}';
    }


}
