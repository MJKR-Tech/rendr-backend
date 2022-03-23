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

    @Override
    public List<DataTemplate> getTemplates() {
        LOG.info("Getting all templates.");
        
        List<DataTemplate> templates = dataTemplateRepository.findAll(Sort.by(Sort.Direction.ASC, "templateId"));
        LOG.info(templates.size() + " templates found: " + templates);
        return templates;
    }

    @Override
    public boolean uploadTemplateFromFile(MultipartFile file) {
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

        //template
        DataTemplate dataTemplate = makeDataTemplate(file.getOriginalFilename());
        ArrayList<DataSheet> listDataSheet = new ArrayList<DataSheet>();

        int sheetCount = workbook.getNumberOfSheets();
        for (int i = 0; i < sheetCount; i++) {

            Sheet datatypeSheet = workbook.getSheetAt(i);
            Iterator<Row> iterator = datatypeSheet.iterator();

            Sheet sheet = workbook.getSheetAt(i);

            //datasheet
            DataSheet dataSheet = makeDataSheet(sheet.getSheetName());
            ArrayList<DataTable> listDataTable = new ArrayList<>();
            LOG.info("Now reading sheet #" + i + " " + sheet.getSheetName());
            while (iterator.hasNext()) {

                long orderNumber = 0;
                Row currentRow = iterator.next();
                Iterator<Cell> cellIterator = currentRow.iterator();

                //datatable
                DataTable dataTable = makeDataTable(currentRow.getRowNum(), 0);
                ArrayList<DataHeader> listDataHeader = new ArrayList<>();
                while (cellIterator.hasNext()) {
                    //dataheader
                    DataHeader dataHeader = makeDataHeader("headerName", orderNumber++);
                    Cell currentCell = cellIterator.next();

                    dataTable.setColNum(currentCell.getColumnIndex());
                    //getCellTypeEnum shown as deprecated for version 3.15
                    //getCellTypeEnum ill be renamed to getCellType starting from version 4.0
                    if (currentCell.getCellType() == CellType.STRING) {
                        String headerName = currentCell.getStringCellValue();
                        dataHeader.setHeaderName(headerName);
                        //save data header
                        listDataHeader.add(dataHeader);
                        System.out.print(headerName + "--");


                    } else if (currentCell.getCellType() == CellType.NUMERIC) {
                        System.out.print(currentCell.getNumericCellValue() + "--");
                    }
                    //save data table
                    dataTable.setDataHeader(listDataHeader);
                }
                //save data sheet

                listDataTable.add(dataTable);
                System.out.println();
            }
            //save data sheet
            dataSheet.setDataTable(listDataTable);
            listDataSheet.add(dataSheet);
        }
        dataTemplate.setDataSheet(listDataSheet);
        dataTemplateRepository.save(dataTemplate);
        return true;
    }

    public DataTemplate makeDataTemplate(String templateName) {
        return new DataTemplate(templateName);
    }

    public DataSheet makeDataSheet(String sheetName) {
        return new DataSheet(sheetName);
    }

    public DataTable makeDataTable(long row, long col) {
        return new DataTable(row, col);
    }

    public DataHeader makeDataHeader(String headerName, long headerOrder) {
        return new DataHeader(headerName, headerOrder);
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
