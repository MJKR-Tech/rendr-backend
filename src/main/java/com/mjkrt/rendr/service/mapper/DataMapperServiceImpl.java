package com.mjkrt.rendr.service.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mjkrt.rendr.entity.DataTable;
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
        
        LOG.info("Obtaining dataTable to tableHolder mappings");
        Map<DataTable, TableHolder> tableToHolderMap = new HashMap<>();
        
        for (DataTable dataTable: tables) {
            // find map DataContainers in dataTable to ColumnHeaders
            // generate subset of TableHolder
            // insert <dataTable, tableHolderSubset> into tableToHolderMap
        }
        return tableToHolderMap;
    }
}
