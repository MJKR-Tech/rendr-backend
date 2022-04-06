package com.mjkrt.rendr.service.mapper;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.mjkrt.rendr.entity.DataCell;
import com.mjkrt.rendr.entity.helper.ColumnHeader;
import com.mjkrt.rendr.entity.helper.TableHolder;

public interface DataMapperService {
    List<TableHolder> generateLinkedTableHolders(long templateId,
            List<ColumnHeader> columnHeaders,
            List<JsonNode> rows);
    Map<Long, TableHolder> generateTableMapping(long templateId,
            List<ColumnHeader> columnHeaders,
            List<TableHolder> linkedTables);
    Map<Long, String> generateCellMapping(List<DataCell> cells, List<TableHolder> linkedTables);
}
