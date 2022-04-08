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
import com.mjkrt.rendr.entity.helper.SortedOrdering;
import com.mjkrt.rendr.utils.LogsCenter;

/**
 * TemplateExtractorServiceImpl.
 * 
 * This class helps to implement the services required in TemplateExtractorService.
 * Rules:
 * - ## a : single cell fill up, aka one-to-one replace
 * - !!(>|v) a : start of container to fill up with direction and alias name
 * - a (++|--) : column a will be sorted by ascending/descending order respectively
 * Overall: (!!(> | v) a [(++|--)]) | ## a
 * 
 * Notes:
 * !! deals with where to start exploring for a table
 * > | v deals with direction substitution
 * ++ | -- deals with sorting columns
 * ## deals with substitution
 */
@Service
public class TemplateExtractorServiceImpl implements TemplateExtractorService {

    private static final Logger LOG = LogsCenter.getLogger(TemplateExtractorServiceImpl.class);

    private static final String CONTAINER_FLAG = "!!";
    
    private static final String HORIZONTAL_CONTAINER_FLAG = "!!>";

    private static final String VERTICAL_CONTAINER_FLAG = "!!v";

    private static final String REPLACE_FLAG = "##";

    private static final String ASC_FLAG = "++";

    private static final String DESC_FLAG = "--";

    /**
     * @inheritDoc
     */
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
            DataSheet dataSheet = processSheet(sheet, i);
            if (dataSheet == null) {
                continue;
            }
            dataSheets.add(dataSheet);
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
        LOG.info("Extracting cells from sheet " + sheet.getSheetName());
        List<DataCell> cells = new ArrayList<>();
        for (Row row : sheet) {
            List<DataCell> rowDataCells = extractCellsFromRow(row);
            cells.addAll(rowDataCells);
        }
        return cells;
    }

    private List<DataCell> extractCellsFromRow(Row row) {
        LOG.info("Extracting cells from rowNum " + row.getRowNum());
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
        LOG.info("Processing cell at " + cell.getAddress());
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
        LOG.info("Extracting containers from sheet " + sheet.getSheetName());
        List<DataContainer> containers = new ArrayList<>();
        for (Row row : sheet) {
            for (Cell cell : row) {
                if (cell == null || cell.getCellType() != STRING) {
                    continue;
                }
                if (cell.getStringCellValue().startsWith(CONTAINER_FLAG)) {
                    containers.add(processSingleContainer(cell));
                }
            }
        }
        return containers;
    }

    private DataContainer processSingleContainer(Cell cell) {
        LOG.info("Processing container at " + cell.getAddress());
        String header = cell.getStringCellValue();
        DataDirection direction = (header.startsWith(HORIZONTAL_CONTAINER_FLAG))
                ? HORIZONTAL
                : VERTICAL;
        header = (direction == HORIZONTAL)
                ? header.substring(HORIZONTAL_CONTAINER_FLAG.length())
                : header.substring(VERTICAL_CONTAINER_FLAG.length());

        SortedOrdering sortBy = SortedOrdering.NOT_USED;
        if (header.endsWith(ASC_FLAG)) {
            header = header.substring(0, header.length() - ASC_FLAG.length());
            sortBy = SortedOrdering.ASC;
        } else if (header.endsWith(DESC_FLAG)) {
            header = header.substring(0, header.length() - DESC_FLAG.length());
            sortBy = SortedOrdering.DESC;
        }
        
        return new DataContainer(direction,
                header.trim(),
                cell.getRowIndex(),
                cell.getColumnIndex(),
                sortBy);
    }
    
    private List<DataTable> groupContainers(List<DataContainer> containers) {
        LOG.info("Grouping containers together");
        List<DataTable> tables = new ArrayList<>();
        tables.addAll(groupContainersByDirection(containers, HORIZONTAL));
        tables.addAll(groupContainersByDirection(containers, VERTICAL));
        return tables;
    }
    
    private List<DataTable> groupContainersByDirection(List<DataContainer> containers,
            DataDirection direction) {

        LOG.info("Grouping containers together by direction " + direction);
        List<DataContainer> trimmedContainers = filterContainersByDirection(containers, direction);
        if (trimmedContainers.isEmpty()) {
            return new ArrayList<>();
        }
        List<List<DataContainer>> groupsOfContainers = new ArrayList<>();
        extractGroupsOfContainers(groupsOfContainers, trimmedContainers, direction);
        return groupsOfContainers.stream()
                .filter(list -> !list.isEmpty())
                .map(DataTable::new)
                .collect(Collectors.toList());
    }

    // assumption made here, tables are split by at least an empty cell in their respective directions
    private void extractGroupsOfContainers(List<List<DataContainer>> groupsOfContainers,
            List<DataContainer> trimmedContainers,
            DataDirection direction) {

        LOG.info("Extracting containers together by direction " + direction);
        List<DataContainer> currentGroup = new ArrayList<>();
        long prevRow = Long.MIN_VALUE;
        long prevCol = Long.MIN_VALUE;
        long ordering = 0;

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
                ordering = 0;
                currentGroup = new ArrayList<>();
            }
            container.setOrdering(ordering);
            currentGroup.add(container);
            prevRow = currRow;
            prevCol = currCol;
            ordering++;
        }
        groupsOfContainers.add(currentGroup);
    }
    
    private List<DataContainer> filterContainersByDirection(List<DataContainer> containers,
            DataDirection direction) {

        LOG.info("Filtering containers together by direction " + direction);
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
