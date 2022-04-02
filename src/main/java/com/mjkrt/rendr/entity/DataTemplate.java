package com.mjkrt.rendr.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class DataTemplate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long templateId;

    @JsonIgnore
    @OneToMany(mappedBy = "dataTemplate", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<DataSheet> dataSheet = new ArrayList<>();

    private String templateName;
    
    private LocalDate dateCreated = LocalDate.now();

    public DataTemplate() {
    }

    public DataTemplate(String templateName) {
        this.templateName = templateName;
    }

    public DataTemplate(long templateId, String templateName) {
        this.templateId = templateId;
        this.templateName = templateName;
    }

    public List<DataSheet> getDataSheet() {
        return dataSheet;
    }

    public void setDataSheet(List<DataSheet> dataSheet) {
        this.dataSheet.clear();
        this.dataSheet.addAll(dataSheet);
        dataSheet.forEach(sheet -> sheet.setDataTemplate(this));
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DataTemplate dataTemplate = (DataTemplate) o;
        return templateId == dataTemplate.templateId
                && Objects.equals(dataSheet, dataTemplate.dataSheet) 
                && Objects.equals(templateName, dataTemplate.templateName)
                && Objects.equals(dateCreated, dataTemplate.dateCreated);
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
