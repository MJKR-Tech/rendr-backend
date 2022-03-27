package com.mjkrt.rendr.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.mjkrt.rendr.entity.ColumnHeader;
import com.mjkrt.rendr.entity.DataTemplate;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.util.Pair;
import org.springframework.web.multipart.MultipartFile;

public interface ExcelService {
    List<DataTemplate> getTemplates();

    boolean uploadTemplateFromFile(MultipartFile file);

    boolean deleteTemplate(long templateId);

    ByteArrayInputStream getSampleTemplate();

    ByteArrayInputStream getTemplate(long templateId);

    ByteArrayInputStream generateExcel(String excelName,
        List<ColumnHeader> headers,
        List<JsonNode> rows);

    Map<Long, Pair<List<ColumnHeader>, Map<String, List<String>>>> generateJsonMapping(
        List<ColumnHeader> headers,
        List<JsonNode> rows);
}
