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

/**
 * DataTemplate.
 *
 * This instance represents an entry in data_template SQL table.
 * It has a one-to-many relationship with DataSheet. 
 */
@Entity
public class DataTemplate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long templateId;

    @JsonIgnore
    @OneToMany(mappedBy = "dataTemplate", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<DataSheet> dataSheets = new ArrayList<>();

    private String templateName;
    
    private LocalDate dateCreated = LocalDate.now();

    public DataTemplate() {
    }

    /**
     * Constructor for DataTemplate.
     * Also ensures bidirectional relationship between the DataSheets fed.
     */
    public DataTemplate(List<DataSheet> dataSheets, String templateName) {
        this.dataSheets = dataSheets;
        this.templateName = templateName;

        dataSheets.forEach(sheet -> sheet.setDataTemplate(this));
    }

    public DataTemplate(long templateId, String templateName) {
        this.templateId = templateId;
        this.templateName = templateName;
    }

    public DataTemplate(String templateName) {
        this.templateName = templateName;
    }

    public List<DataSheet> getDataSheets() {
        return dataSheets;
    }

    /**
     * Sets DataSheets present in instance.
     * Also ensures bidirectional relationship between the DataSheets fed.
     *
     * @param dataSheets list of DataSheets
     */
    public void setDataSheets(List<DataSheet> dataSheets) {
        this.dataSheets.clear();
        this.dataSheets.addAll(dataSheets);
        dataSheets.forEach(sheet -> sheet.setDataTemplate(this));
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
                && Objects.equals(dataSheets, dataTemplate.dataSheets) 
                && Objects.equals(templateName, dataTemplate.templateName)
                && Objects.equals(dateCreated, dataTemplate.dateCreated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(templateId, dataSheets, templateName, dateCreated);
    }

    @Override
    public String toString() {
        return "DataTemplate{" +
                "templateId=" + templateId +
                ", sheet=" + dataSheets +
                ", templateName='" + templateName + '\'' +
                ", dateCreated=" + dateCreated +
                '}';
    }
}
