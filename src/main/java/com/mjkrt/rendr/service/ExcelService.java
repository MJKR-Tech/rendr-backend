package com.mjkrt.rendr.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.mjkrt.rendr.entity.ColumnHeader;
import com.mjkrt.rendr.entity.DataTemplate;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface ExcelService {
    List<DataTemplate> getTemplates();
    boolean uploadTemplateFromFile(MultipartFile file);
    boolean deleteTemplate(long templateId);

    ByteArrayInputStream generateExcel(String excelName, List<ColumnHeader> headers, List<JsonNode> rows);
}
