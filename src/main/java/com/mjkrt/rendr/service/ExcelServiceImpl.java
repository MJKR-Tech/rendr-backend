package com.mjkrt.rendr.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.fasterxml.jackson.databind.JsonNode;
import com.mjkrt.rendr.entity.ColumnHeader;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mjkrt.rendr.utils.LogsCenter;

@Service
public class ExcelServiceImpl implements ExcelService {

    private static final Logger LOG = LogsCenter.getLogger(ExcelServiceImpl.class);

    @Override
    public ByteArrayInputStream generateExcel(String excelName, List<ColumnHeader> headers, List<JsonNode> rows) {
        LOG.info("Generating excel");
        
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(excelName);
        
        int rowCount = 0;
        Row headerRow = sheet.createRow(rowCount++);

        // TODO, sort to custom requirements first or we filter and sort before calling this method
        List<ColumnHeader> selectedHeaders = filterColumns(headers); 
        generateHeaders(workbook, headerRow, selectedHeaders);

        for (JsonNode node : rows) {
            Row dataRow = sheet.createRow(rowCount++);
            addDataToRow(dataRow, selectedHeaders, node);
        }

        IntStream.range(0, headers.size())
                .forEach(sheet::autoSizeColumn);

        return writeToStream(workbook);
    }

    private List<ColumnHeader> filterColumns(List<ColumnHeader> headers) {
        return headers.stream()
                .filter(ColumnHeader::isSelected)
                .collect(Collectors.toList());
    }

    private void generateHeaders(Workbook workbook, Row headerRow, List<ColumnHeader> selectedHeaders) {
        LOG.info("Generating selected headers " + selectedHeaders);
        CellStyle headerCellStyle = generateHeaderStyle(workbook);

        int i = 0;
        for (ColumnHeader header : selectedHeaders) {
            Cell cell = headerRow.createCell(i);
            String headerName = StringUtils.capitalize(
                    StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(header.getName()), ' ')
            );
            
            cell.setCellValue(headerName);
            cell.setCellStyle(headerCellStyle);
            i++;
        }
    }

    private CellStyle generateHeaderStyle(Workbook workbook) {
        LOG.info("Generating header style");
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        return headerCellStyle;
    }

    private void addDataToRow(Row dataRow, List<ColumnHeader> selectedHeaders, JsonNode node) {
        LOG.info("Generating for row " + node);

        int i = 0;
        for (ColumnHeader header : selectedHeaders) {
            String headerName = header.getName();
            Optional<JsonNode> optField = Optional.ofNullable(node.get(headerName));
            dataRow.createCell(i);

            if (optField.isPresent()) {
                JsonNode field = optField.get();
                switch (header.getType()) {
                case DECIMAL:
                    dataRow.createCell(i).setCellValue(field.asInt());
                    break;
                case DOUBLE:
                    dataRow.createCell(i).setCellValue(field.asDouble());
                    break;
                default:
                    // DATE || STRING
                    dataRow.createCell(i).setCellValue(field.asText());
                }
            }
            i++;
        }
    }

    private ByteArrayInputStream writeToStream(Workbook workbook) {
        try {
            LOG.info("Writing to output stream");
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return new ByteArrayInputStream(outputStream.toByteArray());

        } catch (IOException ex) {
            LOG.warning("IOException faced.");
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean readFromFile(MultipartFile file) {
        LOG.info("Reading file " + file.getOriginalFilename() + " as " + file.getOriginalFilename());
        
        LOG.info("File content type: " + file.getContentType());
        List<String> excelTypes = List.of(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // xlsx
                "application/vnd.ms-excel" // xls
        );
        if (file.getContentType() == null || !excelTypes.contains(file.getContentType())) {
            return false;
        }

        Workbook workbook;
        try {
            workbook = (excelTypes.get(0).equals(file.getContentType()))
                    ? new XSSFWorkbook(file.getInputStream())
                    : new HSSFWorkbook(file.getInputStream());
            
        } catch (IOException io) {
            LOG.warning("File is unable to be read.");
            return false;
        }

        int sheetCount = workbook.getNumberOfSheets();
        for (int i = 0; i < sheetCount; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            LOG.info("Now reading sheet #" + i + " " + sheet.getSheetName());
        }
        return true;
    }
}
