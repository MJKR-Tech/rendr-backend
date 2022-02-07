package com.mjkrt.rendr.utils;

import java.util.Arrays;
import java.util.List;

import com.mjkrt.rendr.entity.FinancialData;

public class SampleData {
    
    public static List<FinancialData> getSampleFinancialData() {
        FinancialData data1 = new FinancialData("Head First Java", "Kathy Serria", 79);
        FinancialData data2 = new FinancialData("Effective Java", "Joshua Bloch", 36);
        FinancialData data3 = new FinancialData("Clean Code", "Robert Martin", 42);
        FinancialData data4 = new FinancialData("Thinking in Java", "Bruce Eckel", 35);
        return Arrays.asList(data1, data2, data3, data4);
    }
}
