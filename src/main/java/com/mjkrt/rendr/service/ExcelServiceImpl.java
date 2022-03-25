package com.mjkrt.rendr.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.fasterxml.jackson.databind.JsonNode;

import com.mjkrt.rendr.entity.ColumnHeader;
import com.mjkrt.rendr.entity.DataHeader;
import com.mjkrt.rendr.entity.DataSheet;
import com.mjkrt.rendr.entity.DataTable;
import com.mjkrt.rendr.repository.DataHeaderRepository;
import com.mjkrt.rendr.repository.DataSheetRepository;
import com.mjkrt.rendr.repository.DataTableRepository;
import com.mjkrt.rendr.repository.DataTemplateRepository;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mjkrt.rendr.entity.DataTemplate;
import com.mjkrt.rendr.utils.LogsCenter;

@Service
public class ExcelServiceImpl implements ExcelService {

    private static final Logger LOG = LogsCenter.getLogger(ExcelServiceImpl.class);
    
    @Autowired
    private DataTemplateRepository dataTemplateRepository;

    @Autowired
    private DataSheetRepository dataSheetRepository;

    @Autowired
    private DataTableRepository dataTableRepository;

    @Autowired
    private DataHeaderRepository dataHeaderRepository;

    @Override
    public List<DataTemplate> getTemplates() {
        LOG.info("Getting all templates.");
        
        List<DataTemplate> templates = dataTemplateRepository.findAll(Sort.by(Sort.Direction.ASC, "templateId"));
        LOG.info(templates.size() + " templates found: " + templates);
        return templates;
    }
    
    @Override
    public boolean uploadTemplateFromFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        Optional<DataTemplate> optionalTemplate = Optional.ofNullable(readAsWorkBook(file))
                .map(workbook -> processTemplate(workbook, fileName))
                .map(this::saveTemplate);
        if (optionalTemplate.isEmpty()) {
            return false;
        }
        long savedId = optionalTemplate.get().getTemplateId(); // use id to return so more meaningful
        return true;
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

    private DataTemplate processTemplate(Workbook workbook, String templateName) {
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
        String headerName = cell.getStringCellValue();
        return (headerName.isBlank() || cell.getCellType() != CellType.STRING)
            ? null
            : new DataHeader(headerName, headerOrder);
    }
    
    private DataTemplate saveTemplate(DataTemplate template) {
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
        return dataTemplateRepository.save(template);
    }

    @Override
    public boolean deleteTemplate(long templateId) {
        LOG.info("Delete template with ID "+ templateId);
        DataTemplate toDelete = dataTemplateRepository.getById(templateId);
        
        LOG.info("Deleting template " + toDelete);
        dataTemplateRepository.delete(toDelete);
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
}
