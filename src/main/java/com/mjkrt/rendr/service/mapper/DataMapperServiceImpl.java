package com.mjkrt.rendr.service.mapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.mjkrt.rendr.entity.DataCell;
import com.mjkrt.rendr.entity.DataContainer;
import com.mjkrt.rendr.entity.DataTable;
import com.mjkrt.rendr.entity.helper.ColumnHeader;
import com.mjkrt.rendr.entity.helper.TableHolder;
import com.mjkrt.rendr.service.template.DataTemplateService;
import com.mjkrt.rendr.utils.LogsCenter;

/**
 * DataMapperServiceImpl
 *
 * The implementation of the DataMapperService Interface. Class does the mapping of data from
 * the JSON files to TableHolders.
 */
@Service
public class DataMapperServiceImpl implements DataMapperService {

    private static final Logger LOG = LogsCenter.getLogger(DataMapperServiceImpl.class);

    @Autowired
    private DataTemplateService dataTemplateService;
    
    @Autowired
    private TableHolderService tableHolderService;

    @Autowired
    private JsonService jsonService;

    /**
     * @inheritDoc
     */
    @Override
    public List<TableHolder> generateLinkedTableHolders(long templateId,
            List<ColumnHeader> columnHeaders,
            List<JsonNode> rows) {
        
        LOG.info("Calling generateLinkedTableHolders");
        List<TableHolder> tableHolders = generateTableHolders(columnHeaders, rows);
        return compactTableHolders(tableHolders);
    }

    /**
     * @inheritDoc
     */
    @Override
    public Map<Long, TableHolder> generateTableMapping(long templateId,
            List<ColumnHeader> headers,
            List<TableHolder> linkedTableHolders) {

        LOG.info("Calling generateTableMapping");
        Map<Long, TableHolder> map = new HashMap<>();
        List<DataTable> dataTables = dataTemplateService.findDataTablesWithTemplateId(templateId);

        for (DataTable dataTable : dataTables) {
            long tableId = dataTable.getTableId();
            TableHolder tableHolder = getTemplateTableData(templateId, tableId, linkedTableHolders, headers);
            map.put(tableId, tableHolder);
        }
        return map;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Map<Long, String> generateCellMapping(List<DataCell> cells, List<TableHolder> linkedTables) {
        LOG.info("Calling generateCellMapping");
        Map<Long, String> map = new HashMap<>();
        
        for (DataCell cell : cells) {
            long cellId = cell.getCellId();
            String field = cell.getField();
            String data = "";
            boolean isFound = false;
            
            for (TableHolder table : linkedTables) {
                List<ColumnHeader> headers = table.getColumnHeaders();
                int tableCols = headers.size();
                List<String> topRow = (table.getDataRows().isEmpty())
                        ? new ArrayList<>(Collections.nCopies(tableCols, ""))
                        : table.getDataRows().iterator().next();
                
                for (int idx = 0; idx < headers.size(); idx++) {
                    ColumnHeader header = headers.get(idx);
                    if (field.equals(header.getName())) {
                        data = topRow.get(idx);
                        isFound = true;
                    }
                }
                if (isFound) {
                    break;
                }
            }
            map.put(cellId, data);
        }
        return map;
    }

    /**
     * Returns ColumnHeader with the same header name as the key.
     *
     * @param key String key to be compared against.
     * @param headers The list of ColumnHeaders, with only one containing the same header name as key.
     * @return The ColumnHeader with the same header name as the key.
     */
    private ColumnHeader getColumnHeader(String key, List<ColumnHeader> headers) {
        LOG.info("Calling getColumnHeader");
        key = key.toLowerCase();
        for (ColumnHeader ch : headers) {
            if (key.equalsIgnoreCase(ch.getName())) {
                return cloneColumnHeader(ch);
            }
        }
        return null;
    }

    /**
     * Returns the TableHolder that has all the ColumnHeaders as columnHeaders.
     *
     * @param tableHolders The list of TableHolders.
     * @param columnHeaders Desired list of ColumnHeaders that the TableHolder must contain.
     * @return The TableHolder that has all the columnHeaders.
     */
    private TableHolder findTableHolder(List<TableHolder> tableHolders, List<ColumnHeader> columnHeaders) {
        LOG.info("Calling findTableHolder");
        int size = columnHeaders.size();
        for (TableHolder tableHolder : tableHolders) {
            List<ColumnHeader> thColumnHeader = tableHolder.getColumnHeaders();
            int count = 0;
            for (ColumnHeader columnHeader : thColumnHeader) {
                if (columnHeaders.contains(columnHeader)) {
                    count++;
                }
            }
            if (count == size && size != 0) {
                return tableHolder;
            }
        }
        return null;
    }

    /**
     * Returns the TableHolder with the relevant data information of the table ID specified.
     *
     * @param templateId The template ID which contains the table ID.
     * @param tableId The table ID.
     * @param tableHolders The list of the biggest subsets of JSON files, with relevant information inside.
     * @param headers The ColumnHeaders from all JSON files.
     * @return The TableHolder with the information of the specified table ID.
     */
    private TableHolder getTemplateTableData(long templateId,
            long tableId,
            List<TableHolder> tableHolders,
            List<ColumnHeader> headers) {

        LOG.info("Calling getTemplateTableData");
        List<DataTable> dataTables = dataTemplateService.findDataTablesWithTemplateId(templateId);
        DataTable dataTable = dataTables.stream()
                .filter(dt -> dt.getTableId() == tableId)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("DataTable does not exist in template"));
        
        List<DataContainer> dataContainers = dataTable.getDataContainers();
        List<ColumnHeader> columnHeaders = new ArrayList<>();
        List<ColumnHeader> correctColumnHeaders = new ArrayList<>();
        DataContainer sortedDataContainer = dataTable.getSortByContainer();
        ColumnHeader sortedColumnHeader = new ColumnHeader();

        for (DataContainer container : dataContainers) {
            for (ColumnHeader ch : headers) {
                if (container.equals(sortedDataContainer)
                        && ch.getName().equalsIgnoreCase(container.getAlias())) {
                    sortedColumnHeader = cloneColumnHeader(ch);
                    columnHeaders.add(sortedColumnHeader);
                    correctColumnHeaders.add(sortedColumnHeader);
                    break;
                    
                } else if (ch.getName().equalsIgnoreCase(container.getAlias())) {
                    ColumnHeader newCh = cloneColumnHeader(ch);
                    columnHeaders.add(newCh);
                    correctColumnHeaders.add(newCh);
                    break;
                }
            }
            if (container.getAlias().isEmpty()) {
                columnHeaders.add(ColumnHeader.getMockColumnHeader());
            }
        }
        
        TableHolder th = tableHolderService.generateSubset(
                findTableHolder(tableHolders, correctColumnHeaders),
                columnHeaders);
        th.setSortColumnAndDirection(sortedColumnHeader, sortedDataContainer.getSortBy());
        return th;
    }

    /**
     * Returns the list of TableHolders from the JSON nodes.
     *
     * @param headers The list of ColumnHeaders from all JSON files.
     * @param rows The list of JsonNodes from all JSON files.
     * @return The list of TableHolders from the JSON nodes.
     */
    private List<TableHolder> generateTableHolders(List<ColumnHeader> headers, List<JsonNode> rows) {
        LOG.info("Obtaining TableHolders mappings");
        List<TableHolder> tableHolders = new ArrayList<>();
        for (JsonNode node : rows) {
            List<String> keys = new ArrayList<>();
            Iterator<String> iterator = node.fieldNames();
            iterator.forEachRemaining(keys::add);
            List<ColumnHeader> columnHeaders = new ArrayList<>();
            List<String> strings = new ArrayList<>();
            for (String key : keys) {
                ColumnHeader ch = getColumnHeader(key, headers);
                columnHeaders.add(ch);
                JsonNode dataNode = node.get(key);
                String value = (dataNode.isNumber())
                        ? new BigDecimal(dataNode.asText()).toPlainString()
                        : dataNode.asText("");
                strings.add(value);
            }
            int count = 0;
            for (TableHolder th : tableHolders) {
                if (!th.getColumnHeaders().isEmpty() && th.getColumnHeaders().equals(columnHeaders)) {
                    th.getDataRows().add(strings);
                    count++;
                    break;
                }
            }
            if (count == 0) {
                TableHolder tableHolder = new TableHolder(columnHeaders);
                tableHolder.setDataRow(strings);
                tableHolders.add(tableHolder);
            }
        }
        return tableHolders;
    }

    /**
     * Returns new ColumnHeader object with same information as columnHeader.
     *
     * @param columnHeader the ColumnHeader to be cloned.
     * @return New ColumnHeader object with same information as columnHeader.
     */
    private ColumnHeader cloneColumnHeader(ColumnHeader columnHeader) {
        LOG.info("Calling cloneColumnHeader");
        return new ColumnHeader(columnHeader.getName(),
                columnHeader.getType(),
                columnHeader.getField());
    }

    /**
     * Compares each TableHolder in the list with hone another and returns new list that are subsets of one another.
     *
     * @param tableHolders List of TableHolders.
     * @return Truncated list of TableHolder where each is no longer a subset of another.
     */
    private List<TableHolder> compactTableHolders(List<TableHolder> tableHolders) {
        LOG.info("Calling compactTableHolders");
        if (tableHolders.size() <= 1) {
            return tableHolders;
        }
        Deque<TableHolder> deque = new LinkedList<>(tableHolders);
        boolean hasChanges = true;
        while (hasChanges) {
            hasChanges = popTopAndCompactDeque(deque);
        }
        return new ArrayList<>(deque);
    }

    /**
     * Compares each TableHolder in the list with hone another and returns new list that are subsets of one another.
     *
     * @param deque Queue of TableHolders.
     * @return True if there are changes, false otherwise.
     */
    private boolean popTopAndCompactDeque(Deque<TableHolder> deque) {
        LOG.info("Calling popTopAndCompactDeque");
        boolean hasChanges = false;
        TableHolder intermediateTable = deque.pop(); // pop from top
        for (int i = 0; i < deque.size(); i++) {
            TableHolder nextTable = deque.pop(); // pop from top
            if (tableHolderService.checkIfCanNaturalJoin(intermediateTable, nextTable)) {
                hasChanges = true;
                intermediateTable = tableHolderService.naturalJoin(intermediateTable, nextTable);
                continue;
            }
            deque.add(nextTable); // add to bottom
        }
        deque.add(intermediateTable);
        return hasChanges;
    }
}
