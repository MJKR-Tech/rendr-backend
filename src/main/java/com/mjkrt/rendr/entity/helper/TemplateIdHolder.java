package com.mjkrt.rendr.entity.helper;

import java.util.Objects;

/**
 * TemplateIdHolder.
 *
 * This class represents the ID of an excel template.
 */
public class TemplateIdHolder {
    
    private long templateId;

    public TemplateIdHolder() {
    }

    public TemplateIdHolder(long templateId) {
        this.templateId = templateId;
    }

    public long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(long templateId) {
        this.templateId = templateId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TemplateIdHolder that = (TemplateIdHolder) o;
        return templateId == that.templateId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(templateId);
    }

    @Override
    public String toString() {
        return "TemplateIdHolder{" +
                "templateId=" + templateId +
                '}';
    }
}
