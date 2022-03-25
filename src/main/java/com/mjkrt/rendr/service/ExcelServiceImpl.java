package com.mjkrt.rendr.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.fasterxml.jackson.databind.JsonNode;

import com.mjkrt.rendr.entity.ColumnHeader;
import com.mjkrt.rendr.entity.DataHeader;
import com.mjkrt.rendr.entity.DataSheet;
import com.mjkrt.rendr.entity.DataTable;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mjkrt.rendr.entity.DataTemplate;
import com.mjkrt.rendr.utils.LogsCenter;

@Service
public class ExcelServiceImpl implements ExcelService {

    private static final Logger LOG = LogsCenter.getLogger(ExcelServiceImpl.class);
    
    @Autowired
    private DataTemplateService dataTemplateService;

    @Override
    public List<DataTemplate> getTemplates() {
        LOG.info("Getting all templates");
        
        List<DataTemplate> templates = dataTemplateService.listAll();
        LOG.info(templates.size() + " templates found: " + templates);
        return templates;
    }
    
    @Override
    public boolean uploadTemplateFromFile(MultipartFile file) {
        LOG.info("Uploading file " + file.getOriginalFilename() + " as dataTemplate");
        Optional<DataTemplate> optionalTemplate = Optional.ofNullable(readAsWorkBook(file))
                .map(workbook -> processTemplate(workbook, file.getOriginalFilename()))
                .map(this::saveTemplate);
        return optionalTemplate.isPresent();
    }
    
    private Workbook readAsWorkBook(MultipartFile file) {
        LOG.info("Reading file " + file.getOriginalFilename() + " as " + file.getOriginalFilename());
        LOG.info("File content type: " + file.getContentType());
        List<String> excelTypes = List.of(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // xlsx
                "application/vnd.ms-excel" // xls
        );
        
        if (file.getContentType() == null || !excelTypes.contains(file.getContentType())) {
            LOG.warning("Invalid file type fed");
            return null;
        }
        
        try {
            return (excelTypes.get(0).equals(file.getContentType()))
                    ? new XSSFWorkbook(file.getInputStream())
                    : new HSSFWorkbook(file.getInputStream());
        } catch (IOException io) {
            LOG.warning("Unable to read excel");
            return null;
        }

    }

    private DataTemplate processTemplate(Workbook workbook, String fileName) {
        if (fileName == null) {
            LOG.warning("Filename provided is null");
            return null;
        }
        String templateName = fileName.substring(0, fileName.lastIndexOf('.'));
        
        LOG.info("Processing template " + templateName);
        DataTemplate dataTemplate = new DataTemplate(templateName);
        List<DataSheet> dataSheets = new ArrayList<>();
        
        int sheetCount = workbook.getNumberOfSheets();
        for (int i = 0; i < sheetCount; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            if (sheet == null) {
                continue;
            }
            dataSheets.add(processSheet(sheet));
        }
        
        dataTemplate.setDataSheet(dataSheets);
        return (dataSheets.isEmpty())
                ? null
                : dataTemplate;
    }
    
    private DataSheet processSheet(Sheet sheet) {
        LOG.info("Processing sheet " + sheet.getSheetName());
        DataSheet dataSheet = new DataSheet(sheet.getSheetName());
        Iterator<Row> rowIterator = sheet.iterator();
        List<DataTable> dataTables = new ArrayList<>();
        
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            DataTable table = processHorizontalTable(row);
            if (table == null) {
                continue;
            }
            dataTables.add(table);
        }
        
        dataSheet.setDataTable(dataTables);
        return (dataTables.isEmpty())
                ? null
                : dataSheet;
    }
    
    private DataTable processHorizontalTable(Row currentRow) {
        LOG.info("Processing row " + currentRow.getRowNum());
        long orderNumber = 0;
        int rowNum = currentRow.getRowNum();
        int colNum = -1;
        List<DataHeader> dataHeaders = new ArrayList<>();
        
        for (Cell currentCell : currentRow) {
            DataHeader header = processHeader(currentCell, orderNumber);
            if (header == null) {
                continue;
            }
            colNum = (colNum < 0) ? currentCell.getColumnIndex(): colNum;
            dataHeaders.add(header);
            orderNumber++;
        }
        
        DataTable table = new DataTable(rowNum, colNum);
        table.setDataHeader(dataHeaders);
        return (colNum < 0 || dataHeaders.isEmpty())
                ? null
                : table;
    }
    
    private DataHeader processHeader(Cell cell, long headerOrder) {
        LOG.info("Processing header " + cell.getStringCellValue());
        String headerName = cell.getStringCellValue();
        return (headerName.isBlank() || cell.getCellType() != CellType.STRING)
            ? null
            : new DataHeader(headerName, headerOrder);
    }
    
    private DataTemplate saveTemplate(DataTemplate template) {
        LOG.info("Linking template and recursive entities");
        for (int i = 0; i < template.getDataSheet().size(); i++) {
            DataSheet sheet = template.getDataSheet().get(i);
            
            for (int j = 0; j < sheet.getDataTable().size(); j++) {
                DataTable table = sheet.getDataTable().get(j);
                
                for (int k = 0; k < table.getDataHeader().size(); k++) {
                    DataHeader header = table.getDataHeader().get(k);
                    header.setDataTable(table);
                }
                table.setDataSheet(sheet);
            }
            sheet.setDataTemplate(template);
        }
        LOG.info("Saving template " + template);
        return dataTemplateService.save(template);
    }

    @Override
    public boolean deleteTemplate(long templateId) {
        LOG.info("Delete template with ID "+ templateId);
        dataTemplateService.deleteById(templateId);
        return true;
    }

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

    //Long = table ID
    // Pair<all the column headers with left most as pivot
    // value of pair --> Map of strings
    public Map<Long, Pair<List<ColumnHeader>, Map<String, List<String>>>> generateJsonMapping(List<ColumnHeader> headers, List<JsonNode> rows) {

        Map<Long, Pair<List<ColumnHeader>, Map<String, List<String>>>> map = new HashMap<>();

        long id = 1;
        List<DataSheet> dataSheets = dataTemplateService.findById(id).getDataSheet();
        dataSheets.sort(Comparator.comparingLong(DataSheet::getSheetId));
        List<DataTable> dataTables = dataSheets.get(0).getDataTable(); // todo clean up

        for (DataTable dataTable : dataTables) {
            long tableId = dataTable.getTableId();
            List<DataHeader> dataHeaders = dataTable.getDataHeader();
            List<ColumnHeader> columnHeaders = new ArrayList<>();
            for (DataHeader dataHeader : dataHeaders) {
                for (ColumnHeader ch : headers) {
                    if (ch.getName().equals(dataHeader.getHeaderName())) {
                        columnHeaders.add(ch);
                    }
                }
            }

            int i = 0;
            Map<String, List<String>> strings = new HashMap<>();
            for (ColumnHeader columnHeader : columnHeaders) {
                String headerName = columnHeader.getName();
                List<JsonNode> lstJsonNodes = new ArrayList<>();

                for (JsonNode node : rows) {
                    if (node.findValue(headerName) == null) {
                        continue;
                    }
                    lstJsonNodes.add(node);
                }

                if (i == 0) {
                    for (JsonNode node : lstJsonNodes) {
                        String s = node.get(headerName).asText();
                        strings.put(node.get(headerName).asText(), new ArrayList<>());
                    }
                    i++;
                } else {
                    for (JsonNode node : lstJsonNodes) {
                        for (String key : strings.keySet()) {
                            String ch = columnHeaders.get(0).getName();
                            if (node.has(ch) && node.findValue(ch).asText().equals(key)) {
                                List<String> temp = strings.get(key);
                                temp.add(node.get(headerName).asText());
                            }
                        }
                    }
                }
            }
            Pair<List<ColumnHeader>, Map<String, List<String>>> pair = new Pair<>(columnHeaders, strings);
            map.put(tableId, pair);
        }
        return map;
    }
}
