package com.mjkrt.rendr.service.mapper;

import java.util.List;
import java.util.Map;

import com.mjkrt.rendr.entity.DataTable;
import com.mjkrt.rendr.entity.helper.TableHolder;

public interface DataMapperService {
    Map<DataTable, TableHolder> generateTableToHolderMap(List<DataTable> tables, List<TableHolder> tableHolders);
}
