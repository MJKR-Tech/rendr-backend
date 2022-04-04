package com.mjkrt.rendr.service.mapper;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.mjkrt.rendr.entity.helper.ColumnHeader;
import com.mjkrt.rendr.entity.helper.TableHolder;

public interface JsonService {
    List<TableHolder> getTableHolders(JsonNode jsonNode) throws IOException;
    List<ColumnHeader> getHeaders(JsonNode jsonNode) throws IOException;
    List<JsonNode> getRows(JsonNode jsonNode);
}
