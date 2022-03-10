package com.mjkrt.rendr.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.mjkrt.rendr.entity.ColumnHeader;
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
}
