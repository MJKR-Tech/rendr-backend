package com.mjkrt.rendr.service;

import static com.mjkrt.rendr.entity.DataDirection.HORIZONTAL;
import static com.mjkrt.rendr.entity.DataDirection.VERTICAL;
import static org.apache.poi.ss.usermodel.CellType.STRING;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import com.mjkrt.rendr.entity.DataHeader;
import com.mjkrt.rendr.entity.DataSheet;
import com.mjkrt.rendr.entity.DataTable;
import com.mjkrt.rendr.entity.DataTemplate;
import com.mjkrt.rendr.utils.LogsCenter;

@Service
public class TemplateExtractorServiceImpl implements TemplateExtractorService {

    private static final Logger LOG = LogsCenter.getLogger(TemplateExtractorServiceImpl.class);

    @Override
    public DataTemplate extract(Workbook workbook, String fileName) {
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
}
