package com.mjkrt.rendr.service.mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
    
    public List<ColumnHeader> getHeaders(JsonNode jsonNode) throws IOException {
        LOG.info("Getting headers from jsonNode");
        
        ObjectMapper mapper = new ObjectMapper();
        ObjectReader reader = mapper.readerFor(new TypeReference<List<ColumnHeader>>(){});
        
        Iterator<JsonNode> nodeIterator = jsonNode.iterator();
        Set<ColumnHeader> headerSet = new HashSet<>();

        while (nodeIterator.hasNext()) {
            JsonNode next = nodeIterator.next();
            headerSet.addAll(reader.readValue(next.path("headers")));
        }
        
        List<ColumnHeader> headers = new ArrayList<>(headerSet);
        LOG.info(headers.size() + " headers obtained");
        Collections.sort(headers);
        return headers;
    }

    public List<JsonNode> getRows(JsonNode jsonNode) {
        LOG.info("Getting rows from jsonNode");
        
        Iterator<JsonNode> nodeIterator = jsonNode.iterator();
        List<JsonNode> rows = new ArrayList<>();

        while (nodeIterator.hasNext()) {
            JsonNode next = nodeIterator.next();
            for (JsonNode node : next.path("rows")) {
                rows.add(node);
            }
        }
        
        LOG.info(rows.size() + " rows obtained");
        return rows;
    }

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
}
