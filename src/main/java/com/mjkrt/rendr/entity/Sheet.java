package com.mjkrt.rendr.entity;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

public class Sheet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "sheetId")
    private long sheetId;

    @OneToMany(mappedBy = "sheet")
    private Set<DataTable> dataTable;

    @ManyToOne
    @JoinColumn(name="templateId", nullable=false)
    private Table template;
    private String sheetName;

    private long templateId;

    public void setSheetId(long sheetId) {
        this.sheetId = sheetId;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public void setTemplateId(long templateId) {
        this.templateId = templateId;
    }

    public long getSheetId() {
        return sheetId;
    }

    public String getSheetName() {
        return sheetName;
    }

    public long getTemplateId() {
        return templateId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sheet sheet = (Sheet) o;
        return sheetId == sheet.sheetId && templateId == sheet.templateId && Objects.equals(sheetName, sheet.sheetName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sheetId, sheetName, templateId);
    }

    @Override
    public String toString() {
        return "Sheet{" +
                "sheetId=" + sheetId +
                ", sheetName='" + sheetName + '\'' +
                ", templateId=" + templateId +
                '}';
    }
}
