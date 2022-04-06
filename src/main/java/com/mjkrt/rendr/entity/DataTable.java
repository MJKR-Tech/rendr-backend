package com.mjkrt.rendr.entity;

import java.util.ArrayList;
import java.util.Comparator;
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

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="sheetId", nullable = false)
    private DataSheet dataSheet;

    @OneToMany(mappedBy = "dataTable", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<DataContainer> dataContainers = new ArrayList<>();

    public DataTable() {
    }

    public DataTable(long tableId, DataSheet dataSheet, List<DataContainer> dataContainers) {
        this.tableId = tableId;
        this.dataSheet = dataSheet;
        this.dataContainers = dataContainers;
    }

    public DataTable(List<DataContainer> dataContainers) {
        this.dataContainers = dataContainers;
        dataContainers.forEach(container -> container.setDataTable(this));
    }

    public DataTable(long tableId) {
        this.tableId = tableId;
    }

    public long getTableId() {
        return tableId;
    }

    public void setTableId(long tableId) {
        this.tableId = tableId;
    }

    public DataSheet getDataSheet() {
        return dataSheet;
    }

    public void setDataSheet(DataSheet dataSheet) {
        this.dataSheet = dataSheet;
    }

    public List<DataContainer> getDataContainers() {
        dataContainers.sort(Comparator.comparing(DataContainer::getOrdering));
        return dataContainers;
    }

    public void setDataContainers(List<DataContainer> dataContainers) {
        this.dataContainers.clear();
        this.dataContainers.addAll(dataContainers);
        dataContainers.forEach(container -> container.setDataTable(this));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataTable dataTable = (DataTable) o;
        return tableId == dataTable.tableId
                && Objects.equals(
                        (dataSheet == null) ? null : dataSheet.getSheetId(),
                        (dataTable.dataSheet == null) ? null : dataTable.dataSheet.getSheetId())
                && Objects.equals(dataContainers, dataTable.dataContainers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableId,
                (dataSheet == null) ? null : dataSheet.getSheetId(),
                dataContainers);
    }

    @Override
    public String toString() {
        return "DataTable{" +
                "tableId=" + tableId +
                ", dataSheet=" + ((dataSheet == null) ? "" : dataSheet.getSheetId()) +
                ", dataContainers=" + dataContainers +
                '}';
    }
}
