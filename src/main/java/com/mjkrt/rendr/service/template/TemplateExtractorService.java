package com.mjkrt.rendr.service.template;

import org.apache.poi.ss.usermodel.Workbook;

import com.mjkrt.rendr.entity.DataTemplate;

/**
 * TemplateExtractorService.
 * 
 * This service helps to extract desired data from a given Workbook which will be used to aid in 
 * locating and inputting data during excel data generation after.
 */
public interface TemplateExtractorService {

    /**
     * Extracts a DataTemplate instance from the Workbook specified. 
     * Returns null if Workbook is invalid for extraction.
     * 
     * @param workbook Workbook to extract
     * @param fileName file name of Workbook, to be used as DataTemplate's name as well
     * @return DataTemplate instance if valid, else null
     */
    DataTemplate extract(Workbook workbook, String fileName);
}
