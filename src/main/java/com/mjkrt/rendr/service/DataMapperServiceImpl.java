package com.mjkrt.rendr.service;

import static com.mjkrt.rendr.entity.DataDirection.HORIZONTAL;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.persistence.Table;

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

    public ColumnHeader getColumnHeader(String key, List<ColumnHeader> headers) {
        key = key.toLowerCase();
        for (ColumnHeader ch : headers) {
            String name = ch.getName().toLowerCase();
            if (key.contains(name)) {
                ColumnHeader newCh = cloneColumnHeader(ch);
                return newCh;
            }
        }
        return null;
    }

    public TableHolder findTableHolder(List<TableHolder> tableHolders, List<ColumnHeader> columnHeaders) {
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

    public Map<Long, TableHolder> generateMapping(long templateId, List<ColumnHeader> columnHeaders, List<JsonNode> rows) {
        List<TableHolder> tableHolders = generateTableHolders(columnHeaders, rows);
        List<TableHolder> linkedTableHolders = generateLinkedTableHolders(tableHolders);
        return getMapping(templateId, linkedTableHolders, columnHeaders);
    }
    public Map<Long, TableHolder> getMapping(long templateId, List<TableHolder> linkedTableHolders, List<ColumnHeader> headers) {
        Map<Long, TableHolder> map = new HashMap<>();
        List<DataTable> dataTables = getDataTables(templateId);

        for (DataTable dataTable : dataTables) {
            long tableId = dataTable.getTableId();
            TableHolder tableHolder = getTemplateTableData(templateId, tableId, linkedTableHolders, headers);
            map.put(tableId, tableHolder);
        }
        return map;
    }

    public TableHolder getTemplateTableData(long templateId, long tableId, List<TableHolder> tableHolders,
                              List<ColumnHeader> headers) {
        List<DataTable> dataTables = getDataTables(templateId);
        DataTable dataTable = dataTables.get((int) tableId - 1);
        List<DataHeader> dataHeaders = dataTable.getDataHeader();
        List<ColumnHeader> columnHeaders = new ArrayList<>();
        DataDirection direction = HORIZONTAL;
        List<Pair<Integer, ColumnHeader>> correctColumnHeaders = new ArrayList<>();

        int count = 0;
        for (DataHeader dataHeader : dataHeaders) {
            direction = dataHeader.getDirection();
            for (ColumnHeader ch : headers) {
                if (ch.getName().equals(dataHeader.getHeaderName())) {
                    ColumnHeader newCh = cloneColumnHeader(ch, direction);
                    columnHeaders.add(newCh);
                }
            }
            count++;
            if (columnHeaders.size() + correctColumnHeaders.size() != count) {
                ColumnHeader newCh = new ColumnHeader(dataHeader.getHeaderName(), ColumnHeader.Types.MOCK);
                correctColumnHeaders.add(new Pair<>(count, newCh));
            }
        }
        // may need to add empty strings at placeholder columns todo
        TableHolder th = tableHolderService.generateSubset(findTableHolder(tableHolders, columnHeaders), columnHeaders);

        return fillTableHolderWithMock(th, correctColumnHeaders);
    }

    public TableHolder fillTableHolderWithMock(TableHolder th, List<Pair<Integer, ColumnHeader>> correctColumnHeaders) {
        if (th == null) {
            List<ColumnHeader> chs = new ArrayList<>();
            for (Pair<Integer, ColumnHeader> p : correctColumnHeaders) {
                chs.add(p.getSecond());
            }
            return new TableHolder(chs);
        }

        List<ColumnHeader> currentCHs = th.getColumnHeaders();
        Set<List<String>> set = th.getDataRows();

        for (Pair<Integer, ColumnHeader> p : correctColumnHeaders) {
            int indx = p.getFirst();
            ColumnHeader columnHeader = p.getSecond();
            currentCHs.add(indx, columnHeader);
            for (List<String> strings : set) {
                strings.add(indx, "");
            }
        }
        return th;
    }

    public List<TableHolder> generateLinkedTableHolders(List<TableHolder> tableHolders) {
        int[] lst = new int[tableHolders.size()];
        for (int i = 0; i < tableHolders.size(); i++) {
            lst[i] = 0;
        }

        List<TableHolder> newTableHolders = new ArrayList<>();
        for (int i = 0; i < tableHolders.size(); i++) {
            for (int j = i + 1; j < tableHolders.size(); j++) {
                TableHolder tableHolder = tableHolderService.naturalJoin(tableHolders.get(i), tableHolders.get(j));
                if (tableHolder != null) {
                    lst[i] = 1;
                    lst[j] = 1;
                    newTableHolders.add(tableHolder);
                }
            }
        }

        int count = 0;
        for (int i = 0; i < lst.length; i++) {
            if (lst[i] == 0) {
                count++;
                newTableHolders.add(tableHolders.get(i));
            }
        }

        if (count != tableHolders.size()) {
            return generateLinkedTableHolders(newTableHolders);
        }
        return tableHolders;
    }

    public List<TableHolder> generateTableHolders(List<ColumnHeader> headers, List<JsonNode> rows) {
        LOG.info("Obtaining TableHolders mappings");
        List<TableHolder> tableHolders = new ArrayList<>();
//        TableHolder currentTableHolder = new TableHolder(new ArrayList<ColumnHeader>());
        for (JsonNode node : rows) {
            List<String> keys = new ArrayList<>();
            Iterator<String> iterator = node.fieldNames();
            iterator.forEachRemaining(keys::add);
            List<ColumnHeader> columnHeaders = new ArrayList<>();
            List<String> strings = new ArrayList<>();
            for (String key : keys) {
                ColumnHeader ch = getColumnHeader(key, headers);
                columnHeaders.add(ch);
                String value = node.get(key).asText();
                strings.add(value);
            }
            int count = 0;
            for (TableHolder th : tableHolders) {
                if(!th.getColumnHeaders().isEmpty() && th.getColumnHeaders().equals(columnHeaders)) {
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

    private ColumnHeader cloneColumnHeader(ColumnHeader columnHeader) {
        ColumnHeader ch = new ColumnHeader();
        ch.setField(columnHeader.getField());
        ch.setSelected(columnHeader.isSelected());
        ch.setName(columnHeader.getName());
        ch.setType(columnHeader.getType());
        return ch;
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
