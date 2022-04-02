package com.mjkrt.rendr.service;

import static com.mjkrt.rendr.entity.DataDirection.HORIZONTAL;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.commons.math3.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.mjkrt.rendr.entity.DataDirection;
import com.mjkrt.rendr.entity.DataHeader;
import com.mjkrt.rendr.entity.DataSheet;
import com.mjkrt.rendr.entity.DataTable;
import com.mjkrt.rendr.entity.helper.ColumnHeader;
import com.mjkrt.rendr.entity.helper.TableHolder;
import com.mjkrt.rendr.utils.LogsCenter;

@Service
public class DataMapperServiceImpl implements DataMapperService {

    private static final Logger LOG = LogsCenter.getLogger(DataMapperServiceImpl.class);

    @Autowired
    private DataTemplateService dataTemplateService;
    
    @Autowired
    private TableHolderService tableHolderService;

    @Autowired
    private JsonService jsonService;
    
    // Long = table ID
    // Pair<all the column headers with left most as pivot
    // value of pair --> Map of strings
    @Override
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
                direction = dataHeader.getDirection();
                for (ColumnHeader ch : headers) {
                    if (ch.getName().equals(dataHeader.getHeaderName())) {
                        if (boo) {
                            for (ColumnHeader columnHeader : columnHeaders) {
                                columnHeader.setDirection(direction);
                            }
                            boo = false;
                        }
                        ColumnHeader newCh = cloneColumnHeader(ch, direction);
                        columnHeaders.add(newCh);
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

    private ColumnHeader cloneColumnHeader(ColumnHeader columnHeader, DataDirection dirn) {
        ColumnHeader ch = new ColumnHeader();
        ch.setDirection(dirn);
        ch.setField(columnHeader.getField());
        ch.setSelected(columnHeader.isSelected());
        ch.setName(columnHeader.getName());
        ch.setType(columnHeader.getType());
        return ch;
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
    public Map<Long, TableHolder> generateTableToTableHolderMap(long templateId, List<JsonNode> rows) {
        List<TableHolder> tableHolders = jsonService.getTableHoldersFromRows(rows);
        List<TableHolder> compactTables = compactTableHolders(tableHolders);
        List<DataTable> dataTables = getDataTables(templateId);
        return mapDataTablesToTableHolders(dataTables, compactTables);
    }
    
    private List<TableHolder> compactTableHolders(List<TableHolder> tableHolders) {
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
    
    private boolean popTopAndCompactDeque(Deque<TableHolder> deque) {
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
    
    private Map<Long, TableHolder> mapDataTablesToTableHolders(List<DataTable> dataTables,
            List<TableHolder> tableHolders) {

        Map<Long, TableHolder> tableIdToTableHolderMap = new HashMap<>();
        for (DataTable dataTable: dataTables) {
            // find table from compact tables to map
            // generate headers required in subset
            // generate subset
            // insert <table_id, subset> into map
        }
        return tableIdToTableHolderMap;
    }
}
