package com.mjkrt.rendr.entity;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity
public class DataTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "templateId")
    private long templateId;

    @OneToMany(mappedBy = "template", fetch = FetchType.LAZY)
    private List<DataSheet> dataSheet;

    private String templateName;
    private LocalDate dateCreated;

    public DataTemplate(String templateName) {
        this.templateName = templateName;
        this.dateCreated = LocalDate.now();
    }

    public List<DataSheet> getDataSheet() {
        return dataSheet;
    }

    public void setDataSheet(List<DataSheet> dataSheet) {
        this.dataSheet = dataSheet;
    }

    public long getTemplateId() {
        return templateId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public void setTemplateId(long templateId) {
        this.templateId = templateId;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public void setDateCreated(LocalDate dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataTemplate dataTemplate = (DataTemplate) o;
        return templateId == dataTemplate.templateId && Objects.equals(dataSheet, dataTemplate.dataSheet) && Objects.equals(templateName, dataTemplate.templateName) && Objects.equals(dateCreated, dataTemplate.dateCreated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(templateId, dataSheet, templateName, dateCreated);
    }

    @Override
    public String toString() {
        return "DataTemplate{" +
                "templateId=" + templateId +
                ", sheet=" + dataSheet +
                ", templateName='" + templateName + '\'' +
                ", dateCreated=" + dateCreated +
                '}';
    }
}
