package com.mjkrt.rendr.service.writer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.mjkrt.rendr.entity.DataContainer;
import com.mjkrt.rendr.entity.DataSheet;
import com.mjkrt.rendr.entity.helper.DataDirection;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mjkrt.rendr.entity.DataCell;
import com.mjkrt.rendr.entity.DataTable;
import com.mjkrt.rendr.entity.helper.TableHolder;
import com.mjkrt.rendr.service.template.DataTemplateService;
import com.mjkrt.rendr.utils.LogsCenter;

import static com.mjkrt.rendr.entity.helper.DataDirection.HORIZONTAL;
import static com.mjkrt.rendr.entity.helper.DataDirection.VERTICAL;

@Service
public class DataWriterServiceImpl implements DataWriterService {

    private static final Logger LOG = LogsCenter.getLogger(DataWriterServiceImpl.class);

    @Autowired
    private DataTemplateService dataTemplateService;

    /**
     * @inheritDoc
     */
    @Override
    public void mapDataToWorkbook(Map<Long, TableHolder> dataMap,
            Map<Long, String> cellSubstitutions,
            Workbook workbook, long templateId) {
        
        LOG.info("Mapping data to workbook");
        List<Sheet> sheetList = IntStream.range(0, workbook.getNumberOfSheets())
                .boxed()
                .map(workbook::getSheetAt)
                .collect(Collectors.toList());
        List<DataSheet> dataSheets = dataTemplateService.findDataSheetsWithTemplateId(templateId);
        
        for (Sheet sheetToWrite : sheetList) {
            String sheetName = sheetToWrite.getSheetName();
            Optional<DataSheet> optionalDataSheet = dataSheets.stream()
                    .filter(ds -> ds.getSheetName().equals(sheetName))
                    .findFirst();
            if (optionalDataSheet.isEmpty()) {
                continue;
            }
            DataSheet dataSheet = optionalDataSheet.get();
            writeCellSubstitutionMappings(dataSheet, sheetToWrite, cellSubstitutions);
            writeTableMappings(dataSheet, sheetToWrite, dataMap);

        }
        XSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
    }
    
    private void writeCellSubstitutionMappings(DataSheet dataSheet,
            Sheet sheetToWrite,
            Map<Long, String> cellSubstitutions) {
        
        LOG.info("Writing table mappings");
        List<DataCell> dataCells = dataSheet.getDataCells();
        for (DataCell dc : dataCells) {
            long id = dc.getCellId();
            int r = dc.getRowNum();
            int c = dc.getColNum();
            Row row = sheetToWrite.getRow(r);

            Cell cell = row.getCell(c);
            String dataValue = cellSubstitutions.get(id);
            writeToCell(cell, dataValue, sheetToWrite);
        }
    }
    
    private void writeTableMappings(DataSheet dataSheet,
            Sheet sheetToWrite,
            Map<Long, TableHolder> dataMap) {
        
        LOG.info("Writing substitution cell mappings");
        List<DataTable> dataTables = dataSheet.getDataTables();
        for (DataTable dt : dataTables) {
            List<DataContainer> dtc = dt.getDataContainers();
            Long tableId = dt.getTableId();
            long startRow = dtc.get(0).getRowNum();
            long startCol = dtc.get(0).getColNum();

            TableHolder mapThingData = dataMap.get(tableId);
            DataDirection direction = dtc.get(0).getDirection();
            List<List<String>> data = mapThingData.generateOrderedTable();
            for (List<String> datum : data) {
                if (direction == VERTICAL) {
                    writeHorizontalTable(startRow, startCol, datum, sheetToWrite);
                    startRow += 1;
                } else if (direction == HORIZONTAL) {
                    writeVerticalTable(startRow, startCol, datum, sheetToWrite);
                    startCol += 1;
                }
            }
        }
    }

    private void writeVerticalTable(long startRow,
            long startCol,
            List<String> data, 
            Sheet sheet) {
        
        LOG.info("Writing vertical table at sheet " + sheet.getSheetName()
                + ", starting at (" + startRow + ", " + startCol + ")");
        
        for (String dataValue : data) {
            Row nextRow = sheet.getRow((int) startRow);
            Cell cell = nextRow.getCell((int) startCol, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            writeToCell(cell, dataValue, sheet);
            startRow += 1;
        }
    }

    private void writeHorizontalTable(long startRow,
            long startCol,
            List<String> data,
            Sheet sheet) {
        
        LOG.info("Writing horizontal table at sheet " + sheet.getSheetName()
                + ", starting at (" + startRow + ", " + startCol + ")");
        
        Row row = sheet.getRow((int) startRow);
        for (String dataValue : data) {
            Cell cell = row.getCell((int) startCol, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            writeToCell(cell, dataValue, sheet);
            startCol += 1;
        }
    }

    private Date parseDate(String dataValue) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        format.setLenient(false);
        try {
            return format.parse(dataValue);
        } catch (ParseException e) {
            return null;
        }

    }
    
    private void writeToCell(Cell cell, String dataValue, Sheet sheet) {
        LOG.info("Writing to cell " + cell.getAddress() + " with value " + dataValue);

        if (NumberUtils.isParsable(dataValue) && NumberUtils.isDigits(dataValue)) {
            cell.setCellValue(new BigDecimal(dataValue).longValueExact());
        } else if (NumberUtils.isParsable(dataValue)) {
            cell.setCellValue(new BigDecimal(dataValue).doubleValue());
        } else if (isParsableAsDate(dataValue)) {
            cell.setCellValue(parseDate(dataValue));
        } else {
            cell.setCellValue(dataValue);
        }
        sheet.autoSizeColumn(cell.getColumnIndex());
    }

    /**
     * @inheritDoc
     */
    @Override
    public ByteArrayInputStream writeToStream(Workbook workbook) {
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

    private boolean isParsableAsDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        format.setLenient(false);
        try {
            format.parse(date);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }
}
