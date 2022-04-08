package com.mjkrt.rendr.service.mapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.mjkrt.rendr.entity.helper.ColumnHeader;
import com.mjkrt.rendr.entity.helper.TableHolder;

/**
 * JsonService.
 *
 * This service helps to provide an interface for JsonServices to parse JSON files to JsonNodes and ColumnHeaders.
 */
public interface JsonService {
    /**
     * Returns all the ColumnHeaders of the parsed JSON files.
     *
     * @param jsonNode The JsonNode with relevant ColumnHeaders inside.
     * @return The ColumnHeaders of the parsed JSON files.
     * @throws IOException If an IO operation fails in the intermediary steps.
     */
    List<ColumnHeader> getHeaders(JsonNode jsonNode) throws IOException;

    /**
     * Returns the list of JsonNodes nested within the current JsonNode.
     *
     * @param jsonNode The nested JsonNode.
     * @return The list of JsonNodes.
     */
    List<JsonNode> getRows(JsonNode jsonNode);
}
