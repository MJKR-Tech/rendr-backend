package com.mjkrt.rendr.entity;

import java.util.Objects;

public class ExcelForm {
    
    private String name;

    public ExcelForm() {
    }
    
    public ExcelForm(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExcelForm excelForm = (ExcelForm) o;
        return Objects.equals(name, excelForm.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "ExcelForm{" +
                "name='" + name + '\'' +
                '}';
    }
}
