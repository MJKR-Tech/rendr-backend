package com.mjkrt.rendr.service.mapper;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.mjkrt.rendr.entity.DataCell;
import com.mjkrt.rendr.entity.helper.ColumnHeader;
import com.mjkrt.rendr.entity.helper.TableHolder;

/**
 * DataMapperService.
 *
 * This service helps to provide an interface for DataMappers to generate Data from JSON files and JsonNodes.
 */
public interface DataMapperService {
    /**
     * Returns TableHolders where each TableHolder is the biggest subset of ColumnHeaders with all its relevant
     * information stored inside.
     *
     * @param templateId The template ID.
     * @param columnHeaders The entire list of ColumnHeaders in all JSON files
     * @param rows The JsonNodes parsed.
     * @return TableHolders with all linked data
     */
    List<TableHolder> generateLinkedTableHolders(long templateId,
            List<ColumnHeader> columnHeaders,
            List<JsonNode> rows);

    /**
     * Returns map of each Table ID to each relevant TableHolder.
     *
     * @param templateId The Template ID.
     * @param columnHeaders The entire list of ColumnHeaders in all JSON files.
     * @param linkedTables The TableHolders with each TableHolder being the biggest subset of ColumnHeaders
     *                     with all its relevant information stored inside.
     * @return The map of each Table ID to each relevant TableHolder.
     */
    Map<Long, TableHolder> generateTableMapping(long templateId,
            List<ColumnHeader> columnHeaders,
            List<TableHolder> linkedTables);

    /**
     * Returns map of each cell ID to each relevant datum inside the cell.
     *
     * @param cells The List of Cells to be mapped to.
     * @param linkedTables The TableHolders with relevant data for the cells .
     * @return The map of each cell ID to each relevant datum inside the cell.
     */
    Map<Long, String> generateCellMapping(List<DataCell> cells, List<TableHolder> linkedTables);

}
