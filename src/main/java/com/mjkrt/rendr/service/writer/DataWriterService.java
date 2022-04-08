package com.mjkrt.rendr.service.writer;

import java.io.ByteArrayInputStream;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;

import com.mjkrt.rendr.entity.helper.TableHolder;

/**
 * DataWriterService.
 *
 * This service helps to write parsed data from input files to a given excel workbook.
 */
public interface DataWriterService {

    /**
     * Maps input data to the relevant cells in the given excel template.
     *
     * @param dataMap Map containing the IDs of the tables as key, and the associated tableholder containing all the
     *                relevant information of a table.
     * @param cellSubstitutions Map containing the IDs of a cell as key, and the String data to be written
     *      *                   to that specific cell.
     * @param workbook The excel template to be written to.
     * @param tableId The ID of the table to be written to.
     */
    void mapDataToWorkbook(Map<Long, TableHolder> dataMap,
            Map<Long, String> cellSubstitutions,
            Workbook workbook, long tableId);

    /**
     * Returns the written excel workbook as a stream.
     * @param workbook The excel template to be written to.
     * @return The excel workbook as a stream.
     */
    ByteArrayInputStream writeToStream(Workbook workbook);
}
