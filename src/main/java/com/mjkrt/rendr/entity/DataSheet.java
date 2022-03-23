package com.mjkrt.rendr.entity;

import java.util.List;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class DataSheet {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long sheetId;

    @OneToMany(mappedBy = "sheet", fetch = FetchType.LAZY)
    private List<DataTable> dataTable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="templateId", nullable=false)
    private DataTemplate dataTemplate;
    private String sheetName;

    public DataSheet(String sheetName) {
        this.sheetName = sheetName;
    }

    public List<DataTable> getDataTable() {
        return dataTable;
    }

    public void setDataTable(List<DataTable> dataTable) {
        this.dataTable = dataTable;
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
    public String toString() {
        return "Sheet{" +
                "sheetId=" + sheetId +
                ", dataTable=" + dataTable +
                ", dataTemplate=" + dataTemplate.getTemplateId() +
                ", sheetName='" + sheetName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataSheet sheet = (DataSheet) o;
        return sheetId == sheet.sheetId
                && Objects.equals(dataTable, sheet.dataTable)
                && Objects.equals(dataTemplate.getTemplateId(), sheet.dataTemplate.getTemplateId())
                && Objects.equals(sheetName, sheet.sheetName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sheetId, dataTable, dataTemplate.getTemplateId(), sheetName);
    }
}
