package com.mjkrt.rendr.entity;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.mjkrt.rendr.entity.helper.DataDirection;

public class DataTemplateTest {
    
    private static DataTemplate generateMock() {
        DataContainer containerOne = new DataContainer(1, DataDirection.HORIZONTAL, "slotA", 0, 1);
        DataContainer containerTwo = new DataContainer(3, DataDirection.VERTICAL, "slotB", 30, 50);
        
        DataTable tableOne = new DataTable(1);
        tableOne.setDataContainers(Arrays.asList(containerOne, containerTwo));
        DataSheet sheetOne = new DataSheet(1, "sheet1", 0);
        sheetOne.setDataTables(List.of(tableOne));
        
        DataTemplate templateOnce = new DataTemplate(1, "template1");
        templateOnce.setDataSheets(List.of(sheetOne));
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
        templateTwo.getDataSheets().get(0)
                .getDataTables().get(0)
                .setTableId(3); // modify the lowest level
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
