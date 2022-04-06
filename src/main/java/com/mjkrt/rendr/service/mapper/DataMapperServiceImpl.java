package com.mjkrt.rendr.service.mapper;

import static com.mjkrt.rendr.entity.helper.ColumnHeader.ColumnDataType.MOCK;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.math3.util.Pair;
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

@Service
public class DataMapperServiceImpl implements DataMapperService {

    private static final Logger LOG = LogsCenter.getLogger(DataMapperServiceImpl.class);

    @Autowired
    private DataTemplateService dataTemplateService;
    
    @Autowired
    private TableHolderService tableHolderService;

    @Autowired
    private JsonService jsonService;

    @Override
    public List<TableHolder> generateLinkedTableHolders(long templateId,
            List<ColumnHeader> columnHeaders,
            List<JsonNode> rows) {
        
        LOG.info("Calling generateLinkedTableHolders");
        List<TableHolder> tableHolders = generateTableHolders(columnHeaders, rows);
        return compactTableHolders(tableHolders);
    }
    
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

    @Override
    public Map<Long, String> generateCellMapping(List<DataCell> cells, List<TableHolder> linkedTables) {
        LOG.info("Calling generateCellMapping");
        Map<Long, String> map = new HashMap<>();
        return map;
    }

    private ColumnHeader getColumnHeader(String key, List<ColumnHeader> headers) {
        LOG.info("Calling getColumnHeader");
        key = key.toLowerCase();
        for (ColumnHeader ch : headers) {
            String name = ch.getName().toLowerCase();
            if (key.contains(name)) {
                return cloneColumnHeader(ch);
            }
        }
        return null;
    }

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

    private TableHolder getTemplateTableData(long templateId,
            long tableId,
            List<TableHolder> tableHolders,
            List<ColumnHeader> headers) {
        
        LOG.info("Calling getTemplateTableData");
        List<DataTable> dataTables = dataTemplateService.findDataTablesWithTemplateId(templateId);
        DataTable dataTable = null;
        for (DataTable dt : dataTables) {
            if (dt.getTableId() == tableId) {
                dataTable = dt;
            }
        }
//        DataTable dataTable = dataTables.get((int) tableId);
        List<DataContainer> dataHeaders = dataTable.getDataContainers();
        List<ColumnHeader> columnHeaders = new ArrayList<>();
        List<Pair<Integer, ColumnHeader>> correctColumnHeaders = new ArrayList<>();

        int count = 0;
        for (DataContainer dataHeader : dataHeaders) {
            for (ColumnHeader ch : headers) {
                if (ch.getName().equals(dataHeader.getAlias())) {
                    ColumnHeader newCh = cloneColumnHeader(ch);
                    columnHeaders.add(newCh);
                }
            }
            count++;
            if (columnHeaders.size() + correctColumnHeaders.size() != count) {
                ColumnHeader newCh = new ColumnHeader(dataHeader.getAlias(), MOCK);
                correctColumnHeaders.add(new Pair<>(count, newCh));
            }
        }
        // may need to add empty strings at placeholder columns todo
        return tableHolderService.generateSubset(findTableHolder(tableHolders, columnHeaders), columnHeaders);
        //return fillTableHolderWithMock(th, correctColumnHeaders);
    }

    private TableHolder fillTableHolderWithMock(TableHolder th, List<Pair<Integer, ColumnHeader>> correctColumnHeaders) {
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

    private List<TableHolder> generateLinkedTableHolders(List<TableHolder> tableHolders) {
        LOG.info("Calling generateLinkedTableHolders");
        int[] lst = new int[tableHolders.size()];
        for (int i = 0; i < tableHolders.size(); i++) {
            lst[i] = 0;
        }

        List<TableHolder> newTableHolders = new ArrayList<>();
        for (int i = 0; i < tableHolders.size(); i++) {
            LOG.info("Calling generateLinkedTableHolders 1.1 || size=" + tableHolders.size());
            for (int j = i + 1; j < tableHolders.size(); j++) {
                LOG.info("Calling generateLinkedTableHolders 1.2 || i=" + i + " j=" + j);
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
            LOG.info("Calling generateLinkedTableHolders 2");
            if (lst[i] == 0) {
                count++;
                newTableHolders.add(tableHolders.get(i));
            }
        }

        if (count != tableHolders.size()) {
            LOG.info("Calling generateLinkedTableHolders 3");
            return generateLinkedTableHolders(newTableHolders);
        }
        return tableHolders;
    }

    private List<TableHolder> generateTableHolders(List<ColumnHeader> headers, List<JsonNode> rows) {
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
        LOG.info("Calling cloneColumnHeader");
        ColumnHeader ch = new ColumnHeader();
        ch.setField(columnHeader.getField());
        ch.setName(columnHeader.getName());
        ch.setType(columnHeader.getType());
        return ch;
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
