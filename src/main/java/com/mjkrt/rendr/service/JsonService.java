package com.mjkrt.rendr.service;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.mjkrt.rendr.entity.ColumnHeader;

public interface JsonService {
    List<ColumnHeader> getHeaders(JsonNode jsonNode) throws IOException;
    List<JsonNode> getRows(JsonNode jsonNode);
}
