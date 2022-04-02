package com.mjkrt.rendr.service;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.mjkrt.rendr.entity.helper.ColumnHeader;
import com.mjkrt.rendr.entity.helper.TableHolder;

public interface JsonService {
    List<ColumnHeader> getHeaders(JsonNode jsonNode) throws IOException;
    List<JsonNode> getRows(JsonNode jsonNode);
    List<TableHolder> getTableHoldersFromRows(List<JsonNode> jsonNode);
}
