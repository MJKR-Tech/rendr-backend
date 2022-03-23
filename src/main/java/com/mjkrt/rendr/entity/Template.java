package com.mjkrt.rendr.entity;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
public class Template {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "templateId")
    private long templateId;

    @OneToMany(mappedBy = "template", fetch = FetchType.LAZY)
    private List<Sheet> sheet;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Template template = (Template) o;
        return templateId == template.templateId && Objects.equals(sheet, template.sheet) && Objects.equals(templateName, template.templateName) && Objects.equals(dateCreated, template.dateCreated);
    }

    @Override
    public int hashCode() {
        return Objects.hash(templateId, sheet, templateName, dateCreated);
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

    //    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Template template = (Template) o;
//        return templateId == template.templateId && Objects.equals(templateName, template.templateName) && Objects.equals(dateCreated, template.dateCreated);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(templateId, templateName, dateCreated);
//    }
//
//    @Override
//    public String toString() {
//        return "Template{" +
//                "templateId=" + templateId +
//                ", templateName='" + templateName + '\'' +
//                ", dateCreated=" + dateCreated +
//                '}';
//    }
}
