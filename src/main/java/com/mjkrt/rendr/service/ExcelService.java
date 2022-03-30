package com.mjkrt.rendr.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.mjkrt.rendr.entity.helper.ColumnHeader;
import com.mjkrt.rendr.entity.DataTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.util.Pair;
import org.springframework.web.multipart.MultipartFile;

public interface ExcelService {
    String EXCEL_EXT = ".xlsx"; // interface fields are set as public static final
    
    List<DataTemplate> getTemplates();

    boolean uploadTemplateFromFile(MultipartFile file);

    boolean deleteTemplate(long templateId);
    
    String getFileNameForTemplate(long templateId);

    ByteArrayInputStream getSampleTemplate() throws IOException;

    ByteArrayInputStream getTemplate(long templateId) throws IOException;

    ByteArrayInputStream generateExcel(long templateId,
        List<ColumnHeader> headers,
        List<JsonNode> rows) throws IOException;

    Map<Long, Pair<List<ColumnHeader>, Map<String, List<String>>>> generateJsonMapping(
        long templateId,
        List<ColumnHeader> headers,
        List<JsonNode> rows);
    
    void deleteAllTemplates();
}
