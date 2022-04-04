package com.mjkrt.rendr.service.mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.mjkrt.rendr.entity.helper.ColumnHeader;
import com.mjkrt.rendr.entity.helper.TableHolder;
import com.mjkrt.rendr.utils.LogsCenter;

@Service
public class JsonServiceImpl implements JsonService {

    private static final Logger LOG = LogsCenter.getLogger(JsonServiceImpl.class);
    
    @Override
    public List<TableHolder> getTableHolders(JsonNode jsonNode) throws IOException {
        LOG.info("Mapping JsonNode to TableHolders");
        List<TableHolder> tableHolders = new ArrayList<>();
        for (JsonNode childNode : jsonNode) {
            TableHolder tableHolder = mapSingleTableHolder(childNode);
            tableHolders.add(tableHolder);
        }
        return tableHolders;
    }

    private TableHolder mapSingleTableHolder(JsonNode node) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectReader reader = mapper.readerFor(new TypeReference<List<ColumnHeader>>(){});
        
        List<ColumnHeader> headers = reader.readValue(node.path("headers"));
        List<String> headerLabels = headers.stream()
                .map(ColumnHeader::getField)
                .collect(Collectors.toList());
        
        Set<List<String>> matrix = new HashSet<>();
        for (JsonNode rowData : node.path("rows")) {
            List<String> currentRow = new ArrayList<>();
            for (String headerLabel : headerLabels) {
                String associatedData = rowData.path(headerLabel).asText();
                currentRow.add(associatedData);
            }
            matrix.add(currentRow);
        }
        return new TableHolder(headers, matrix);
    }

    @Override
    public Map<String, String> getSingleCellSubstitutions(JsonNode jsonNode) throws IOException {
        Map<String, String> substitutionMap = new HashMap<>();
        // todo figure out structure of json for single substitution and add logic in
        return substitutionMap;
    }
}
