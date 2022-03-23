package com.mjkrt.rendr.entity;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
public class DataSheet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "sheetId")
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
        DataSheet dataSheet = (DataSheet) o;
        return sheetId == dataSheet.sheetId && Objects.equals(dataTable, dataSheet.dataTable) && Objects.equals(dataTemplate.getTemplateId(), dataSheet.dataTemplate.getTemplateId()) && Objects.equals(sheetName, dataSheet.sheetName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sheetId, dataTable, dataTemplate.getTemplateId(), sheetName);
    }
}
