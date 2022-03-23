package com.mjkrt.rendr.entity;

import java.sql.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class DataTemplate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long templateId;

    @OneToMany(mappedBy = "template", fetch = FetchType.LAZY)
    private List<DataSheet> sheet;

    private String templateName;
    
    private Date dateCreated;

    public long getTemplateId() {
        return templateId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setTemplateId(long templateId) {
        this.templateId = templateId;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
    public String toString() {
        return "Template{" +
                "templateId=" + templateId +
                ", sheet=" + sheet +
                ", templateName='" + templateName + '\'' +
                ", dateCreated=" + dateCreated +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataTemplate template = (DataTemplate) o;
        return templateId == template.templateId
                && Objects.equals(sheet, template.sheet)
                && Objects.equals(templateName, template.templateName)
                && Objects.equals(dateCreated, template.dateCreated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(templateId, sheet, templateName, dateCreated);
    }
}
