package com.mjkrt.rendr.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.mjkrt.rendr.entity.ColumnHeader;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface ExcelService {
    ByteArrayInputStream generateExcel(String excelName, List<ColumnHeader> headers, List<JsonNode> rows);
    boolean readFromFile(MultipartFile file);
}
