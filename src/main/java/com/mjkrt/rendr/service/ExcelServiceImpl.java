package com.mjkrt.rendr.service;

import static com.mjkrt.rendr.entity.DataDirection.HORIZONTAL;
import static com.mjkrt.rendr.entity.DataDirection.VERTICAL;
import static org.apache.poi.ss.usermodel.CellType.STRING;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.xml.crypto.Data;

import com.fasterxml.jackson.databind.JsonNode;

import com.mjkrt.rendr.entity.DataDirection;
import com.mjkrt.rendr.entity.helper.ColumnHeader;
import com.mjkrt.rendr.entity.DataHeader;
import com.mjkrt.rendr.entity.DataSheet;
import com.mjkrt.rendr.entity.DataTable;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.math3.util.Pair;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.mjkrt.rendr.entity.DataTemplate;
import com.mjkrt.rendr.utils.LogsCenter;

@Transactional
@Service
public class ExcelServiceImpl implements ExcelService {

    private static final Logger LOG = LogsCenter.getLogger(ExcelServiceImpl.class);
    
    @Autowired
    private DataTemplateService dataTemplateService;
    
    @Autowired
    private FileService fileService;

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

        try {
            long templateId = optionalTemplate.map(DataTemplate::getTemplateId).orElseThrow();
            fileService.save(file, templateId + EXCEL_EXT);
            return true;
            
        } catch (Exception e) {
            LOG.warning(e.getLocalizedMessage());
            return false;
        }
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
            List<DataTable> tables = processHorizontalTables(row);
            if (tables.isEmpty()) {
                continue;
            }
            dataTables.addAll(tables);
        }
        
        List<DataTable> mergedTables = mergeTables(dataTables);
        dataSheet.setDataTable(mergedTables);
        return (mergedTables.isEmpty())
                ? null
                : dataSheet;
    }
    
    private List<DataTable> mergeTables(List<DataTable> tables) {
        List<DataTable> newTables = new ArrayList<>();
        tables.sort(Comparator.comparing(DataTable::getColNum).thenComparing(DataTable::getRowNum));

        Iterator<DataTable> dataTableIterator = tables.iterator();
        while (dataTableIterator.hasNext()) {
            DataTable table = dataTableIterator.next();
            long col = table.getColNum();
            long row = table.getRowNum();
            List<DataTable> groupedTables = new ArrayList<>();

            while (table.getColNum() == col) {
                if (table.getRowNum() == row) {
                    groupedTables.add(table);
                } else {
                    break;
                }
                row++;
                if (!dataTableIterator.hasNext()) {
                    break;
                }
                table = dataTableIterator.next();
            }

            DataTable newTable = groupedTables.get(0);
            newTables.add(newTable);
            if (groupedTables.size() == 1) {
                continue;
            }
            if (newTable.getDataHeader().size() == 1) {
                newTable.getDataHeader().get(0).setDirection(VERTICAL);
            }
            int ordering = 1;
            for (int i = 1; i < groupedTables.size(); i++) {
                DataTable toMergeTable = groupedTables.get(i);
                assert toMergeTable.getDataHeader().size() == 1;

                DataHeader header = toMergeTable.getDataHeader().get(0);
                header.setDataTable(newTable);
                header.setDirection(VERTICAL);
                header.setHeaderOrder(ordering);
                newTable.addDataHeader(header);
                ordering++;
            }
        }
        if (newTables.get(0).getDataHeader().get(0).getDirection() == VERTICAL) {
            for (DataTable dt : newTables) {
                for (DataHeader dh : dt.getDataHeader()) {
                    dh.setDirection(VERTICAL);
                }
            }
            return newTables;
        }
        for (DataTable dt : tables) {
            for (DataHeader dh : dt.getDataHeader()) {
                dh.setDirection(HORIZONTAL);
            }
        }
        return tables;
    }
    
    private List<DataTable> processHorizontalTables(Row currentRow) {
        LOG.info("Processing row " + currentRow.getRowNum());
        
        List<DataTable> dataTables = new ArrayList<>();
        Iterator<Cell> cellIterator = currentRow.iterator();
        while (cellIterator.hasNext()) {
            List<DataTable> tables = new ArrayList<>();
            processSingleHorizontal(cellIterator, tables, null);
            tables = tables.stream().filter(Objects::nonNull).collect(Collectors.toList());
            if (tables.isEmpty()) {
                continue;
            }
            dataTables.addAll(tables);
        }
        return dataTables;
    }

    private void processSingleHorizontal(Iterator<Cell> cellIterator, List<DataTable> tables, Cell originalCurrentCell) {
        long orderNumber = 0;
        int rowNum = -1;
        int colNum = -1;
        int previousCol = -1;
        List<DataHeader> dataHeaders = new ArrayList<>();

        if (originalCurrentCell != null) {
            DataHeader header = processHeader(originalCurrentCell, orderNumber);
            if (header == null) {

            } else {
                rowNum = (rowNum < 0) ? originalCurrentCell.getRowIndex() : rowNum;
                colNum = (colNum < 0) ? originalCurrentCell.getColumnIndex() : colNum;
                dataHeaders.add(header);
                orderNumber++;
            }
        }

        while (cellIterator.hasNext()) {
            Cell currentCell = cellIterator.next();
            int currentCol = currentCell.getColumnIndex();

            if (previousCol != -1 && previousCol + 1 != currentCol) {
                processSingleHorizontal(cellIterator, tables, currentCell);
                break;
            }

            DataHeader header = processHeader(currentCell, orderNumber);
            if (header == null) {
                break;
            }
            rowNum = (rowNum < 0) ? currentCell.getRowIndex() : rowNum;
            colNum = (colNum < 0) ? currentCell.getColumnIndex() : colNum;
            dataHeaders.add(header);
            previousCol = currentCol;
            orderNumber++;
        }

        DataTable table = new DataTable(rowNum, colNum);
        table.setDataHeader(dataHeaders);
        tables.add((colNum < 0 || dataHeaders.isEmpty())
                ? null
                : table);
    }
    
    private DataHeader processHeader(Cell cell, long headerOrder) {
        LOG.info("Processing header " + cell.toString());
        if (cell.getCellType() != STRING) {
            return null;
        }
        String headerName = cell.getStringCellValue();
        return (headerName.isBlank() || cell.getCellType() != STRING)
            ? null
            : new DataHeader(headerName, headerOrder);
    }
    
    private DataTemplate saveTemplate(DataTemplate template) {
        LOG.info("Saving template " + template);
        return dataTemplateService.save(template);
    }

    @Override
    public boolean deleteTemplate(long templateId) {
        LOG.info("Delete template with ID "+ templateId);
        dataTemplateService.deleteById(templateId);
        fileService.delete(templateId + EXCEL_EXT);
        return true;
    }

    @Override
    public String getFileNameForTemplate(long templateId) {
        DataTemplate template = dataTemplateService.findById(templateId);
        return template.getTemplateName();
    }

    @Override
    public ByteArrayInputStream getSampleTemplate() throws IOException {
        LOG.info("Obtaining sample template");
        Resource sampleResource = fileService.loadSample();
        byte[] byteArray = IOUtils.toByteArray(sampleResource.getInputStream());
        return new ByteArrayInputStream(byteArray);
    }

    @Override
    public ByteArrayInputStream getTemplate(long templateId) throws IOException {
        LOG.info("Obtaining template with ID "+ templateId);
        Resource sampleResource = fileService.load(templateId + EXCEL_EXT);
        byte[] byteArray = IOUtils.toByteArray(sampleResource.getInputStream());
        return new ByteArrayInputStream(byteArray);
    }

    @Override
    public ByteArrayInputStream generateExcel(long templateId, 
            List<ColumnHeader> headers,
            List<JsonNode> rows) throws IOException {
        
        LOG.info("Generating excel for template ID " + templateId);
        
        Resource templateResource = fileService.load(templateId + EXCEL_EXT);
        Workbook workbook = new XSSFWorkbook(templateResource.getInputStream());
        Map<Long, Pair<List<ColumnHeader>, Map<String, List<String>>>> dataMap =
                generateJsonMapping(templateId, headers, rows);
        mapDataToWorkbook(templateId, dataMap, workbook);
        
        return writeToStream(workbook);
    }
    
    private void mapDataToWorkbook(long templateId,
            Map<Long, Pair<List<ColumnHeader>, Map<String, List<String>>>> dataMap,
            Workbook workbook) {

        LOG.info("Mapping data to workbook");
        
        int sheetCount = workbook.getNumberOfSheets();
        ArrayList<Sheet> sheetList = new ArrayList<>();
        for (int i = 0; i < sheetCount; i++) {
            sheetList.add(workbook.getSheetAt(i));
        }

        List<DataSheet> dataSheets = dataTemplateService.findById(templateId).getDataSheet();

        for (Sheet sheet : sheetList) {
            String sheetName = sheet.getSheetName();
            DataSheet dataSheet = new DataSheet();
            for (DataSheet ds : dataSheets) {
                if (ds.getSheetName().equals(sheetName)) {
                    dataSheet = ds;
                    break;
                }
            }

            List<DataTable> dataTables = dataSheet.getDataTable();
            for (DataTable dt : dataTables) {
                Long tableId = dt.getTableId();
                long startRow = dt.getRowNum();
                long startCol = dt.getColNum();

                Pair<List<ColumnHeader>, Map<String, List<String>>> mapThingData = dataMap.get(tableId);
                Map<String, List<String>> mapThingValues = mapThingData.getValue();
                

                for (Map.Entry<String, List<String>> entry : mapThingValues.entrySet()) {
                    int col = (int) startCol;
                    startRow += 1;
                    Row row = sheet.getRow((int) startRow);
                    Cell cell = row.getCell(col++, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

                    if (NumberUtils.isParsable(entry.getKey())) {
                        cell.setCellValue(Integer.parseInt(entry.getKey()));
                    } else {
                        cell.setCellValue(entry.getKey());
                    }

                    List<String> dataValues = entry.getValue();

                    for (String dataValue : dataValues) {
                        cell = row.getCell(col++, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        if (NumberUtils.isParsable(dataValue)) {
                            if (NumberUtils.isDigits(dataValue)) {
                                cell.setCellValue(Integer.parseInt(dataValue));
                            } else {
                                cell.setCellValue(Double.parseDouble(dataValue));
                            }
                        } else {
                            cell.setCellValue(dataValue);
                        }
                    }
                }

            }
        }
        XSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
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

    // Long = table ID
    // Pair<all the column headers with left most as pivot
    // value of pair --> Map of strings
    public Map<Long, Pair<List<ColumnHeader>, Map<String, List<String>>>> generateJsonMapping(
            long templateId,
            List<ColumnHeader> headers,
            List<JsonNode> rows) {

        LOG.info("Obtaining json mappings");
        Map<Long, Pair<List<ColumnHeader>, Map<String, List<String>>>> map = new HashMap<>();
        List<DataTable> dataTables = getDataTables(templateId);

        for (DataTable dataTable : dataTables) {
            long tableId = dataTable.getTableId();
            List<DataHeader> dataHeaders = dataTable.getDataHeader();
            List<ColumnHeader> columnHeaders = new ArrayList<>();
            DataDirection direction = HORIZONTAL;
            boolean boo = true;

            int count = 0;
            for (DataHeader dataHeader : dataHeaders) {
                for (ColumnHeader ch : headers) {
                    direction = ch.getDirection();
                    if (ch.getName().equals(dataHeader.getHeaderName())) {
                        if (boo) {
                            for (ColumnHeader columnHeader : columnHeaders) {
                                columnHeader.setDirection(direction);
                            }
                            boo = false;
                        }
                        columnHeaders.add(ch);
                    }
                }
                count++;

                if (columnHeaders.size() != count) {
                    ColumnHeader newCh = new ColumnHeader(dataHeader.getHeaderName());
                    columnHeaders.add(newCh);
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
                        strings.put(s, new ArrayList<>());
                    }
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
                    for (String key : strings.keySet()) {
                        List<String> temp = strings.get(key);
                        if (temp.size() != i) {
                            temp.add("");
                        }
                    }
                }
                i++;
                Pair<List<ColumnHeader>, Map<String, List<String>>> pair = new Pair<>(columnHeaders, strings);
                map.put(tableId, pair);
            }
        }
        return map;
    }

    private List<DataTable> getDataTables(long templateId) {
        LOG.info("Obtaining tables from template ID " + templateId);
        
        List<DataSheet> dataSheets = dataTemplateService.findById(templateId).getDataSheet();
        dataSheets.sort(Comparator.comparingLong(DataSheet::getSheetId));
        List<DataTable> dataTables = new ArrayList<>();
        for (DataSheet ds : dataSheets) {
            dataTables.addAll(ds.getDataTable());
        }
        return dataTables;
    }

    @Override
    public void deleteAllTemplates() {
        dataTemplateService.deleteAll();
    }
}
