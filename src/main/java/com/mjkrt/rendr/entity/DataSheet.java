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

/**
 * DataSheet.
 *
 * This instance represents an entry in data_sheet SQL table.
 * It has a one-to-many relationship with DataTable. 
 * It has a one-to-many relationship with DataCell. 
 * It has a many-to-one relationship with DataTemplate. 
 */
@Entity
public class DataSheet {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long sheetId;

    @OneToMany(mappedBy = "dataSheet", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<DataTable> dataTables = new ArrayList<>();

    @OneToMany(mappedBy = "dataSheet", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<DataCell> dataCells = new ArrayList<>();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "templateId", nullable = false)
    private DataTemplate dataTemplate;
    
    private String sheetName;

    private long sheetOrder;

    public DataSheet() {
    }

    /**
     * Constructor for DataSheet.
     * Also ensures bidirectional relationship between the DataTables and DataCells fed.
     */
    public DataSheet(long sheetId,
            List<DataTable> dataTables,
            List<DataCell> dataCells,
            DataTemplate dataTemplate,
            String sheetName,
            long sheetOrder) {
        
        this.sheetId = sheetId;
        this.dataTables = dataTables;
        this.dataCells = dataCells;
        this.dataTemplate = dataTemplate;
        this.sheetName = sheetName;
        this.sheetOrder = sheetOrder;

        dataTables.forEach(table -> table.setDataSheet(this));
        dataCells.forEach(cell -> cell.setDataSheet(this));
    }

    /**
     * Constructor for DataSheet.
     * Also ensures bidirectional relationship between the DataTables and DataCells fed.
     */
    public DataSheet(List<DataTable> dataTables, List<DataCell> dataCells, String sheetName, long sheetOrder) {
        this.dataTables = dataTables;
        this.dataCells = dataCells;
        this.sheetName = sheetName;
        this.sheetOrder = sheetOrder;

        dataTables.forEach(table -> table.setDataSheet(this));
        dataCells.forEach(cell -> cell.setDataSheet(this));
    }

    public DataSheet(long sheetId, String sheetName, long sheetOrder) {
        this.sheetId = sheetId;
        this.sheetName = sheetName;
        this.sheetOrder = sheetOrder;
    }

    public long getSheetId() {
        return sheetId;
    }

    public void setSheetId(long sheetId) {
        this.sheetId = sheetId;
    }

    public List<DataTable> getDataTables() {
        return dataTables;
    }

    /**
     * Sets DataTables present in instance.
     * Also ensures bidirectional relationship between the DataTables fed.
     * 
     * @param dataTables list of DataTables
     */
    public void setDataTables(List<DataTable> dataTables) {
        this.dataTables.clear();
        this.dataTables.addAll(dataTables);
        dataTables.forEach(container -> container.setDataSheet(this));
    }

    public List<DataCell> getDataCells() {
        return dataCells;
    }

    /**
     * Sets DataCells present in instance.
     * Also ensures bidirectional relationship between the DataCells fed.
     * 
     * @param dataCells list of DataCells
     */
    public void setDataCells(List<DataCell> dataCells) {
        this.dataCells.clear();
        this.dataCells.addAll(dataCells);
        dataCells.forEach(cell -> cell.setDataSheet(this));
    }

    public DataTemplate getDataTemplate() {
        return dataTemplate;
    }

    public void setDataTemplate(DataTemplate dataTemplate) {
        this.dataTemplate = dataTemplate;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public long getSheetOrder() {
        return sheetOrder;
    }

    public void setSheetOrder(long sheetOrder) {
        this.sheetOrder = sheetOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataSheet dataSheet = (DataSheet) o;
        return sheetId == dataSheet.sheetId
                && sheetOrder == dataSheet.sheetOrder
                && Objects.equals(dataTables, dataSheet.dataTables)
                && Objects.equals(dataCells, dataSheet.dataCells)
                && Objects.equals(
                        (dataTemplate == null) ? null : dataTemplate.getTemplateId(),
                        (dataSheet.dataTemplate == null) ? null : dataSheet.dataTemplate.getTemplateId())
                && Objects.equals(sheetName, dataSheet.sheetName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sheetId,
                dataTables,
                dataCells,
                (dataTemplate == null) ? null : dataTemplate.getTemplateId(),
                sheetName,
                sheetOrder);
    }

    @Override
    public String toString() {
        return "DataSheet{" +
                "sheetId=" + sheetId +
                ", dataTables=" + dataTables +
                ", dataCells=" + dataCells +
                ", dataTemplate=" + ((dataTemplate == null) ? "" : dataTemplate.getTemplateId()) +
                ", sheetName='" + sheetName + '\'' +
                ", sheetOrder=" + sheetOrder +
                '}';
    }
}