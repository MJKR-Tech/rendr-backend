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
public class DataSheet {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long sheetId;

    @OneToMany(mappedBy = "dataSheet", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<DataTable> dataTable = new ArrayList<>();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name="templateId", nullable=false)
    private DataTemplate dataTemplate;
    
    private String sheetName;

    private long sheetOrder;

    public long getSheetOrder() {
        return sheetOrder;
    }

    public void setSheetOrder(long sheetOrder) {
        this.sheetOrder = sheetOrder;
    }

    public DataSheet(String sheetName, long sheetOrder) {
        this.sheetName = sheetName;
        this.sheetOrder = sheetOrder;
    }

    public DataSheet() {
    }

    public DataSheet(String sheetName) {
        this.sheetName = sheetName;
    }

    public List<DataTable> getDataTable() {
        return dataTable;
    }

    public void setDataTable(List<DataTable> dataTable) {
        this.dataTable.clear();
        this.dataTable.addAll(dataTable);
        dataTable.forEach(table -> table.setDataSheet(this));
    }

    public DataTemplate getDataTemplate() {
        return dataTemplate;
    }

    public void setDataTemplate(DataTemplate dataTemplate) {
        this.dataTemplate = dataTemplate;
    }

    public void setSheetId(long sheetId) {
        this.sheetId = sheetId;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public long getSheetId() {
        return sheetId;
    }

    public String getSheetName() {
        return sheetName;
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
                && Objects.equals(dataTable, dataSheet.dataTable)
                && Objects.equals(dataTemplate.getTemplateId(), dataSheet.dataTemplate.getTemplateId())
                && Objects.equals(sheetName, dataSheet.sheetName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sheetId, dataTable, dataTemplate.getTemplateId(), sheetName, sheetOrder);
    }

    @Override
    public String toString() {
        return "DataSheet{" +
                "sheetId=" + sheetId +
                ", dataTable=" + dataTable +
                ", dataTemplate=" + ((dataTemplate == null) ? "" : dataTemplate.getTemplateId()) +
                ", sheetName='" + sheetName + '\'' +
                ", sheetOrder=" + sheetOrder +
                '}';
    }
}