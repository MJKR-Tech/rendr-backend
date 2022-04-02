package com.mjkrt.rendr.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.math3.util.Pair;

import com.fasterxml.jackson.databind.JsonNode;
import com.mjkrt.rendr.entity.helper.ColumnHeader;

public interface DataMapperService {
    Map<Long, Pair<List<ColumnHeader>, Map<String, List<String>>>> generateJsonMapping(
            long templateId,
            List<ColumnHeader> headers,
            List<JsonNode> rows);
    
}
