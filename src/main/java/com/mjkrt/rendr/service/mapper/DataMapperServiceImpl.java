package com.mjkrt.rendr.service.mapper;

import java.util.ArrayList;
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
import com.mjkrt.rendr.entity.DataTable;
import com.mjkrt.rendr.entity.helper.ColumnHeader;
import com.mjkrt.rendr.entity.helper.TableHolder;
import com.mjkrt.rendr.utils.LogsCenter;

@Service
public class DataMapperServiceImpl implements DataMapperService {

    private static final Logger LOG = LogsCenter.getLogger(DataMapperServiceImpl.class);

    @Autowired
    private TableHolderService tableHolderService;
    
    // Long = table ID
    // Pair<all the column headers with left most as pivot
    // value of pair --> Map of strings
    @Override
    public Map<DataTable, TableHolder> generateTableToHolderMap(List<DataTable> tables,
            List<TableHolder> tableHolders) {
        
        LOG.info("Obtaining json mappings");
        Map<Long, Pair<List<ColumnHeader>, Map<String, List<String>>>> map = new HashMap<>();
//        List<DataTable> dataTables = getDataTables(templateId);
//
//        for (DataTable dataTable : dataTables) {
//            long tableId = dataTable.getTableId();
//            List<DataContainer> dataContainers = dataTable.getDataHeaders();
//            List<ColumnHeader> columnHeaders = new ArrayList<>();
//            DataDirection direction = HORIZONTAL;
//            boolean boo = true;
//
//            int count = 0;
//            for (DataContainer dataContainer : dataContainers) {
//                direction = dataContainer.getDirection();
//                for (ColumnHeader ch : headers) {
//                    if (ch.getName().equals(dataContainer.getHeaderName())) {
//                        if (boo) {
//                            for (ColumnHeader columnHeader : columnHeaders) {
//                                columnHeader.setDirection(direction);
//                            }
//                            boo = false;
//                        }
//                        ColumnHeader newCh = cloneColumnHeader(ch, direction);
//                        columnHeaders.add(newCh);
//                    }
//                }
//                count++;
//
//                if (columnHeaders.size() != count) {
//                    ColumnHeader newCh = new ColumnHeader(dataContainer.getHeaderName());
//                    columnHeaders.add(newCh);
//                }
//            }
//
//            int i = 0;
//            Map<String, List<String>> strings = new HashMap<>();
//            for (ColumnHeader columnHeader : columnHeaders) {
//                String headerName = columnHeader.getName();
//                List<JsonNode> lstJsonNodes = new ArrayList<>();
//
//                for (JsonNode node : rows) {
//                    if (node.findValue(headerName) == null) {
//                        continue;
//                    }
//                    lstJsonNodes.add(node);
//                }
//
//                if (i == 0) {
//                    for (JsonNode node : lstJsonNodes) {
//                        String s = node.get(headerName).asText();
//                        strings.put(s, new ArrayList<>());
//                    }
//                } else {
//                    for (JsonNode node : lstJsonNodes) {
//                        for (String key : strings.keySet()) {
//                            String ch = columnHeaders.get(0).getName();
//                            if (node.has(ch) && node.findValue(ch).asText().equals(key)) {
//                                List<String> temp = strings.get(key);
//                                temp.add(node.get(headerName).asText());
//                            }
//                        }
//                    }
//                    for (String key : strings.keySet()) {
//                        List<String> temp = strings.get(key);
//                        if (temp.size() != i) {
//                            temp.add("");
//                        }
//                    }
//                }
//                i++;
//                Pair<List<ColumnHeader>, Map<String, List<String>>> pair = new Pair<>(columnHeaders, strings);
//                map.put(tableId, pair);
//            }
//        }
        return new HashMap<>();
    }

//    private ColumnHeader cloneColumnHeader(ColumnHeader columnHeader, DataDirection dirn) {
//        ColumnHeader ch = new ColumnHeader();
//        ch.setDirection(dirn);
//        ch.setField(columnHeader.getField());
//        ch.setSelected(columnHeader.isSelected());
//        ch.setName(columnHeader.getName());
//        ch.setType(columnHeader.getType());
//        return ch;
//    }
    
    public Map<Long, TableHolder> generateTableToTableHolderMap(long templateId, List<JsonNode> rows) {
//        List<TableHolder> tableHolders = jsonService.getTableHoldersFromRows(rows);
//        List<TableHolder> compactTables = compactTableHolders(tableHolders);
//        List<DataTable> dataTables = getDataTables(templateId);
//        return mapDataTablesToTableHolders(dataTables, compactTables);
        return new HashMap<>();
    }
    
    private Map<DataTable, TableHolder> mapTablesToHolders(List<DataTable> tables, List<TableHolder> holders) {

        Map<DataTable, TableHolder> tableIdToTableHolderMap = new HashMap<>();
        for (DataTable dataTable: tables) {
            // find map DataContainers in dataTable to ColumnHeaders
            // generate subset of TableHolder
            // insert <dataTable, tableHolderSubset> into map
        }
        return tableIdToTableHolderMap;
    }
}
