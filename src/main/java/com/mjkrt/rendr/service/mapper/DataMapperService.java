package com.mjkrt.rendr.service.mapper;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.mjkrt.rendr.entity.DataTable;
import com.mjkrt.rendr.entity.helper.ColumnHeader;
import com.mjkrt.rendr.entity.helper.TableHolder;

public interface DataMapperService {
    Map<Long, TableHolder> generateTableIdToTableHolderMap(List<DataTable> tables, List<TableHolder> holders);
    Map<Long, TableHolder> generateMapping(long templateId,
            List<ColumnHeader> columnHeaders,
            List<JsonNode> rows);
}
