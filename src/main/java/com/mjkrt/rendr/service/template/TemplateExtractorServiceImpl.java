package com.mjkrt.rendr.service.template;

import static com.mjkrt.rendr.entity.helper.DataDirection.HORIZONTAL;
import static com.mjkrt.rendr.entity.helper.DataDirection.VERTICAL;
import static org.apache.poi.ss.usermodel.CellType.STRING;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import com.mjkrt.rendr.entity.DataCell;
import com.mjkrt.rendr.entity.DataContainer;
import com.mjkrt.rendr.entity.DataTable;
import com.mjkrt.rendr.entity.helper.DataDirection;
import com.mjkrt.rendr.entity.DataSheet;
import com.mjkrt.rendr.entity.DataTemplate;
import com.mjkrt.rendr.utils.LogsCenter;

/*
Syntax:
(A | B) - exclusively A or B

## a - single cell fill up, aka one-to-one replace
!!(>|v) a - start of container to fill up with direction and alias name

overall: (## | !!(> | v)) a

# is used for single cell fill up (desc at top of excel files)
!! deals with where to start exploring for a table
> | v deals with direction substitution
## helps with substitution
*/
@Service
public class TemplateExtractorServiceImpl implements TemplateExtractorService {

    private static final Logger LOG = LogsCenter.getLogger(TemplateExtractorServiceImpl.class);

    private static final String TABLE_FLAG = "!!";
    
    private static final String HORIZONTAL_TABLE_FLAG = "!!>";

    private static final String VERTICAL_TABLE_FLAG = "!!v";

    private static final String REPLACE_FLAG = "##";

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
            dataSheets.add(processSheet(sheet, i));
        }
        dataTemplate.setDataSheets(dataSheets);
        return (dataSheets.isEmpty())
                ? null
                : dataTemplate;
    }

    private DataSheet processSheet(Sheet sheet, int ordering) {
        LOG.info("Processing sheet " + sheet.getSheetName());
        
        String sheetName = sheet.getSheetName();
        List<DataCell> dataCells = extractCellsFromSheet(sheet);
        List<DataContainer> dataContainers = extractContainersFromSheet(sheet);
        if (dataCells.isEmpty() && dataContainers.isEmpty()) {
            return null;
        }
        List<DataTable> dataTables = groupContainers(dataContainers);
        return new DataSheet(dataTables, dataCells, sheetName, ordering);
    }
    
    private List<DataCell> extractCellsFromSheet(Sheet sheet) {
        List<DataCell> cells = new ArrayList<>();
        for (Row row : sheet) {
            List<DataCell> rowDataCells = extractCellsFromRow(row);
            cells.addAll(rowDataCells);
        }
        return cells;
    }

    private List<DataCell> extractCellsFromRow(Row row) {
        List<DataCell> rowDataCells = new ArrayList<>();
        for (Cell cell : row) {
            DataCell dataCell = processAsDataCell(cell);
            if (dataCell == null) {
                continue;
            }
            rowDataCells.add(dataCell);
        }
        return rowDataCells;
    }
    
    private DataCell processAsDataCell(Cell cell) {
        if (cell == null || cell.getCellType() != STRING) {
            return null;
        }
        String cellValue = cell.getStringCellValue();
        if (!cellValue.startsWith(REPLACE_FLAG)) {
            return null;
        }
        String field = cellValue.substring(REPLACE_FLAG.length()).trim();
        int rowNum = cell.getRowIndex();
        int colNum = cell.getColumnIndex();
        return new DataCell(field, rowNum, colNum);
    }

    private List<DataContainer> extractContainersFromSheet(Sheet sheet) {
        List<DataContainer> containers = new ArrayList<>();
        for (Row row : sheet) {
            for (Cell cell : row) {
                if (cell == null || cell.getCellType() != STRING) {
                    continue;
                }
                if (cell.getStringCellValue().startsWith(TABLE_FLAG)) {
                    containers.add(processSingleContainer(cell));
                }
            }
        }
        return containers;
    }

    private DataContainer processSingleContainer(Cell cell) {
        String header = cell.getStringCellValue();
        DataDirection direction = (header.startsWith(HORIZONTAL_TABLE_FLAG))
                ? HORIZONTAL
                : VERTICAL;
        header = (direction == HORIZONTAL)
                ? header.substring(HORIZONTAL_TABLE_FLAG.length())
                : header.substring(VERTICAL_TABLE_FLAG.length());
        return new DataContainer(direction,
                header.trim(),
                cell.getRowIndex(),
                cell.getColumnIndex());
    }
    
    private List<DataTable> groupContainers(List<DataContainer> containers) {
        List<DataTable> tables = new ArrayList<>();
        tables.addAll(groupContainersByDirection(containers, HORIZONTAL));
        tables.addAll(groupContainersByDirection(containers, VERTICAL));
        return tables;
    }
    
    // assumption made here, tables are split by at least an empty cell in their respective directions
    private List<DataTable> groupContainersByDirection(List<DataContainer> containers,
            DataDirection direction) {
        
        List<DataContainer> trimmedContainers = filterContainersByDirection(containers, direction);
        if (trimmedContainers.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<List<DataContainer>> groupsOfContainers = new ArrayList<>();
        List<DataContainer> currentGroup = new ArrayList<>();
        long prevRow = Long.MIN_VALUE;
        long prevCol = Long.MIN_VALUE;
        
        for (DataContainer container : trimmedContainers) {
            long currRow = container.getRowNum();
            long currCol = container.getColNum();
            boolean isNotInit = (prevRow > 0) && (prevCol > 0);
            boolean isHorizontalAndJumped = isNotInit
                    && (direction == HORIZONTAL)
                    && (currCol != prevCol || currRow > prevRow + 1);
            boolean isVerticalAndJumped = isNotInit
                    && (direction == VERTICAL)
                    && (currRow != prevRow || currCol > prevCol + 1);
            if (isHorizontalAndJumped || isVerticalAndJumped) {
                groupsOfContainers.add(currentGroup);
                currentGroup = new ArrayList<>();
            }
            currentGroup.add(container);
            prevRow = currRow;
            prevCol = currCol;
        }
        
        groupsOfContainers.add(currentGroup);        
        return groupsOfContainers.stream()
                .filter(list -> !list.isEmpty())
                .map(DataTable::new)
                .collect(Collectors.toList());
    }
    
    private List<DataContainer> filterContainersByDirection(List<DataContainer> containers,
            DataDirection direction) {
        
        Comparator<DataContainer> comparator = (direction == HORIZONTAL)
                ? Comparator.comparing(DataContainer::getRowNum)
                        .thenComparing(DataContainer::getColNum)
                : Comparator.comparing(DataContainer::getColNum)
                        .thenComparing(DataContainer::getRowNum);
        return containers.stream()
                .filter(container -> container.getDirection() == direction)
                .sorted(comparator)
                .collect(Collectors.toList());
    }
}
