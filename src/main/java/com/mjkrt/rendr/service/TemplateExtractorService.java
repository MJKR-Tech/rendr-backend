package com.mjkrt.rendr.service;

import org.apache.poi.ss.usermodel.Workbook;

import com.mjkrt.rendr.entity.DataTemplate;

public interface TemplateExtractorService {
    DataTemplate extract(Workbook workbook, String fileName);
}
