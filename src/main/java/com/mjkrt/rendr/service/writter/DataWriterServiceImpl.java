package com.mjkrt.rendr.service.writter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.mjkrt.rendr.entity.DataContainer;
import com.mjkrt.rendr.entity.DataSheet;
import com.mjkrt.rendr.entity.helper.ColumnHeader;
import com.mjkrt.rendr.entity.helper.DataDirection;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
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

    // todo add one more field for single cell replacements
    @Override
    public void mapDataToWorkbook(Map<Long, TableHolder> dataMap,
            Map<Long, String> cellSubstitutions,
            Workbook workbook, long templateId) {
        
        LOG.info("Mapping data to workbook");
        int sheetCount = workbook.getNumberOfSheets();
        ArrayList<Sheet> sheetList = new ArrayList<>();
        for (int i = 0; i < sheetCount; i++) {
            sheetList.add(workbook.getSheetAt(i));
        }

        List<DataSheet> dataSheets = dataTemplateService.findById(templateId).getDataSheets();

        for (Sheet sheet : sheetList) {
            String sheetName = sheet.getSheetName();
            DataSheet dataSheet = new DataSheet();
            for (DataSheet ds : dataSheets) {
                if (ds.getSheetName().equals(sheetName)) {
                    dataSheet = ds;
                    break;
                }
            }

            List<DataTable> dataTables = dataSheet.getDataTables();
            for (DataTable dt : dataTables) {
                List<DataContainer> dtc = dt.getDataContainers();
                Long tableId = dt.getTableId();
                long startRow = dtc.get(0).getRowNum();
                long startCol = dtc.get(0).getColNum();

                TableHolder mapThingData = dataMap.get(tableId);
                DataDirection direction = dtc.get(0).getDirection();
                Set<List<String>> datas = mapThingData.getDataRows();

                if (direction == VERTICAL) {
                    for (List<String> data : datas) {
                        startRow += 1;
                        writeHorizontalTable(startRow, startCol, data, sheet);

                    }
                } else if (direction == HORIZONTAL){
                    for (List<String> data : datas) {
                        startCol += 1;
                        writeVerticalTable(startRow, startCol, data, sheet);
                    }
                }

            }
        }
        XSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);
    }

    private void writeVerticalTable(long startRowNum, long colNum, List<String> data, Sheet sheet) {

        for (String dataValue : data) {
            Row nextRow = sheet.getRow((int) startRowNum);

            Cell cell = nextRow.getCell((int)colNum, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
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

    private void writeHorizontalTable(long startRow, long startCol, List<String> data, Sheet sheet) {
        int col = (int) startCol;
        Row row = sheet.getRow((int) startRow);

        for (String dataValue : data) {
            Cell cell = row.getCell(col++, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
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
