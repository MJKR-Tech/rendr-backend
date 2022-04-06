package com.mjkrt.rendr.tools;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.mjkrt.rendr.entity.DataSheet;
import com.mjkrt.rendr.entity.DataTemplate;

public class MockDataTemplate {

    private long templateId = 0;
    private String templateName = "MockDataTemplate";
    private List<DataSheet> dataSheet = new ArrayList<>();
    private LocalDate dateCreated = LocalDate.now();

    public MockDataTemplate() {
    }

    public static MockDataTemplate create() {
        return new MockDataTemplate();
    }

    public MockDataTemplate withTemplateId(long id) {
        this.templateId = id;
        return this;
    }

    public MockDataTemplate withTemplateName(String name) {
        this.templateName = name;
        return this;
    }

    public MockDataTemplate withDataSheets(List<DataSheet> dataSheets) {
        this.dataSheet = dataSheets;
        return this;
    }

    public MockDataTemplate withDateCreated(LocalDate date) {
        this.dateCreated = date;
        return this;
    }

    public DataTemplate generate() {
        DataTemplate newTemplate = new DataTemplate();
        newTemplate.setTemplateId(this.templateId);
        newTemplate.setTemplateName(this.templateName);
        newTemplate.setDataSheets(this.dataSheet);
        newTemplate.setDateCreated(this.dateCreated);
        return newTemplate;
    }
}
