package com.mjkrt.rendr.service.writter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mjkrt.rendr.entity.DataTable;
import com.mjkrt.rendr.entity.helper.TableHolder;
import com.mjkrt.rendr.service.template.DataTemplateService;
import com.mjkrt.rendr.utils.LogsCenter;

@Service
public class DataWriterServiceImpl implements DataWriterService {

    private static final Logger LOG = LogsCenter.getLogger(DataWriterServiceImpl.class);

    @Autowired
    private DataTemplateService dataTemplateService;

    // todo add one more field for single cell replacements
    @Override
    public void mapDataToWorkbook(Map<DataTable, TableHolder> dataMap, Workbook workbook) {
        LOG.info("Mapping data to workbook");
//
//        int sheetCount = workbook.getNumberOfSheets();
//        ArrayList<Sheet> sheetList = new ArrayList<>();
//        for (int i = 0; i < sheetCount; i++) {
//            sheetList.add(workbook.getSheetAt(i));
//        }
//
//        List<DataSheet> dataSheets = dataTemplateService.findById(templateId).getDataSheets();
//
//        for (Sheet sheet : sheetList) {
//            String sheetName = sheet.getSheetName();
//            DataSheet dataSheet = new DataSheet();
//            for (DataSheet ds : dataSheets) {
//                if (ds.getSheetName().equals(sheetName)) {
//                    dataSheet = ds;
//                    break;
//                }
//            }
//
//            List<DataTable> dataTables = dataSheet.getDataTables();
//            for (DataTable dt : dataTables) {
//                Long tableId = dt.getTableId();
//                long startRow = dt.getRowNum();
//                long startCol = dt.getColNum();
//
//                Pair<List<ColumnHeader>, Map<String, List<String>>> mapThingData = dataMap.get(tableId);
//                List<ColumnHeader> columnHeaders = mapThingData.getKey();
//                DataDirection direction = columnHeaders.get(0).getDirection();
//                Map<String, List<String>> mapThingValues = mapThingData.getValue();
//
//                if (direction == HORIZONTAL) {
//                    for (Map.Entry<String, List<String>> entry : mapThingValues.entrySet()) {
//                        startRow += 1;
//                        writeHorizontalTable(startRow, startCol, entry, sheet);
//
//                    }
//                } else if (direction == VERTICAL){
//                    for (Map.Entry<String, List<String>> entry : mapThingValues.entrySet()) {
//                        startCol += 1;
//                        writeVerticalTable(startRow, startCol, entry, sheet);
//                    }
//                }
//
//            }
//        }
//        XSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
    }

    private void writeVerticalTable(long startRowNum, long colNum, Map.Entry<String, List<String>> entry, Sheet sheet) {
        Row startingRow = sheet.getRow((int) startRowNum);
        startRowNum += 1;
        Cell cell = startingRow.getCell((int)colNum, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

        if (NumberUtils.isParsable(entry.getKey())) {
            cell.setCellValue(Integer.parseInt(entry.getKey()));
        } else {
            cell.setCellValue(entry.getKey());
        }

        List<String> dataValues = entry.getValue();

        for (String dataValue : dataValues) {
            Row nextRow = sheet.getRow((int) startRowNum);

            cell = nextRow.getCell((int)colNum, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            if (NumberUtils.isParsable(dataValue)) {
                if (NumberUtils.isDigits(dataValue)) {
                    cell.setCellValue(Integer.parseInt(dataValue));
                } else {
                    cell.setCellValue(Double.parseDouble(dataValue));
                }
            } else {
                cell.setCellValue(dataValue);
            }
            startRowNum += 1;
        }
    }

    private void writeHorizontalTable(long startRow,
            long startCol,
            Map.Entry<String, List<String>> entry,
            Sheet sheet) {
        
        int col = (int) startCol;
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
}
