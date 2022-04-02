package com.mjkrt.rendr.entity;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DataTemplateTest {
    
    private static DataTemplate generateMock() {
        DataHeader headerOne = new DataHeader(1, DataDirection.HORIZONTAL, "header1", 0);
        DataHeader headerTwo = new DataHeader(2, DataDirection.HORIZONTAL, "header2", 1);
        DataHeader headerThree = new DataHeader(3, DataDirection.VERTICAL, "header3", 0);
        
        DataTable tableOne = new DataTable(1, 10, 15);
        DataTable tableTwo = new DataTable(2, 0, 5);
        tableOne.addDataHeader(headerOne);
        tableOne.addDataHeader(headerTwo);
        tableTwo.addDataHeader(headerThree);
        
        DataSheet sheetOne = new DataSheet(1, "sheet1", 0);
        sheetOne.setDataTable(Arrays.asList(tableOne, tableTwo));
        
        DataTemplate templateOnce = new DataTemplate(1, "template1");
        templateOnce.setDataSheet(List.of(sheetOne));
        return templateOnce;
    }
    
    // test bi-directional relationship
    @Test
    public void equals_isTrue_noStackOverflowError() {
        DataTemplate templateOne = generateMock();
        DataTemplate templateTwo = generateMock();
        Assertions.assertEquals(templateOne, templateTwo);
    }
    
    @Test
    public void equals_isFalse_noStackOverflowError() {
        DataTemplate templateOne = generateMock();
        DataTemplate templateTwo = generateMock();
        templateTwo.getDataSheet().get(0)
                .getDataTable().get(0)
                .getDataHeader().get(0)
                .setHeaderName("Different Header"); // modify the lowest level
        Assertions.assertNotEquals(templateOne, templateTwo);
    }

    @Test
    public void hashCode_isTrue_noStackOverflowError() {
        DataTemplate templateOne = generateMock();
        DataTemplate templateTwo = generateMock();
        Assertions.assertEquals(templateOne.hashCode(), templateTwo.hashCode());
    }

    @Test
    public void toString_isTrue_noStackOverflowError() {
        DataTemplate templateOne = generateMock();
        DataTemplate templateTwo = generateMock();
        Assertions.assertEquals(templateOne.toString(), templateTwo.toString());
    }
}
