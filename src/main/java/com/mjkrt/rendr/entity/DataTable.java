package com.mjkrt.rendr.entity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
import com.mjkrt.rendr.entity.helper.SortedOrdering;

/**
 * DataTable.
 *
 * This instance represents an entry in data_table SQL table.
 * It has a one-to-many relationship with DataContainer. 
 * It has a many-to-one relationship with DataSheet. 
 */
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

    /**
     * Constructor for DataSheet.
     * Also ensures bidirectional relationship between the DataContainers fed.
     */
    public DataTable(long tableId, DataSheet dataSheet, List<DataContainer> dataContainers) {
        this.tableId = tableId;
        this.dataSheet = dataSheet;
        this.dataContainers = dataContainers;
        dataContainers.forEach(container -> container.setDataTable(this));
    }

    /**
     * Constructor for DataSheet.
     * Also ensures bidirectional relationship between the DataContainers fed.
     */
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

    /**
     * Returns a list of DataContainers sorted by ordering
     * 
     * @return list of DataContainers sorted by ordering
     */
    public List<DataContainer> getDataContainers() {
        dataContainers.sort(Comparator.comparing(DataContainer::getOrdering));
        return dataContainers;
    }

    /**
     * Returns the first DataContainer with a valid SortedOrdering parameter (i.e. not NOT_USED) if present.
     * Else, the first instance will be returned.
     * This method assumes there is at least one DataContainer instance present.
     * 
     * @return first instance of a non-NOT_USED DataContainer present in sorted order, else the first instance in list 
     */
    public DataContainer getSortByContainer() {
        if (dataContainers.isEmpty()) {
            throw new IllegalStateException("A DataTable should have at least one DataContainer.");
        }
        Optional<DataContainer> dataContainerOptional = getDataContainers().stream()
                .filter(container -> container.getSortBy() != SortedOrdering.NOT_USED)
                .findFirst();
        return dataContainerOptional.orElseGet(() -> getDataContainers().get(0));
    }

    /**
     * Sets DataContainers present in instance.
     * Also ensures bidirectional relationship between the DataContainers fed.
     */
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
