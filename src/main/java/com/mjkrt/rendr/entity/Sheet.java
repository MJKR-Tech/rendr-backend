package com.mjkrt.rendr.entity;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
public class Sheet {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "sheetId")
    private long sheetId;

    @OneToMany(mappedBy = "sheet", fetch = FetchType.LAZY)
    private Set<DataTable> dataTable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="templateId", nullable=false)
    private Template template;
    private String sheetName;

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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sheet sheet = (Sheet) o;
        return sheetId == sheet.sheetId && Objects.equals(sheetName, sheet.sheetName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sheetId, sheetName);
    }

    @Override
    public String toString() {
        return "Sheet{" +
                "sheetId=" + sheetId +
                ", sheetName='" + sheetName + '\'' +
                '}';
    }
}
