package com.mjkrt.rendr.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.mjkrt.rendr.entity.DataTemplate;
import com.mjkrt.rendr.entity.helper.TemplateIdHolder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

/**
 * ExcelService.
 *
 * This service provides excel related interactions for the excel controller to tap onto.
 */
public interface ExcelService {
    String EXCEL_EXT = ".xlsx"; // interface fields are set as public static final
    
    List<DataTemplate> getTemplates();

    TemplateIdHolder uploadTemplateFromFile(MultipartFile file);

    /**
     * Deletes the excel templates corresponding to the given template IDs.
     * @param templateIds The IDs of templates to be deleted.
     * @return A boolean to inform if deletion is successful.
     */
    boolean deleteTemplate(List<Long> templateIds);

    /**
     * Deletes all stored excel templates.
     */
    void deleteAllTemplates();

    /**
     * Retrieves the filename of a template corressponding to the given template ID.
     *
     * @param templateId The template ID of the template to get filename of.
     * @return The filename of the template.
     */
    String getFileNameForTemplate(long templateId);

    /**
     * Returns a sample excel template as a byte stream.
     *
     * @return A byte stream representing the template.
     * @throws IOException If writing fails.
     */
    ByteArrayInputStream getSampleTemplate() throws IOException;

    /**
     * Returns the excel template corressponding to the given template ID as a byte stream.
     *
     * @param templateId The template ID of the excel template to be retrieved.
     * @return A byte stream representing the template.
     * @throws IOException If writing fails.
     */
    ByteArrayInputStream getTemplate(long templateId) throws IOException;

    /**
     * Returns the desired output excel with all relevant data written, as a byte stream.
     *
     * @param dataNode Json node holding relevant data and parameters for mapping.
     * @return The desired output excel as a byte stream.
     * @throws IOException If writing fails.
     */
    ByteArrayInputStream generateExcel(JsonNode dataNode) throws IOException;

    /**
     * Copies the excel byte stream to the response message to be sent to frontend.
     * @param response Desired http response.
     * @param stream Excel byte stream to write.
     * @param fileName Filename for excel to write.
     * @throws IOException If writing fails.
     */
    void copyByteStreamToResponse(HttpServletResponse response,
            ByteArrayInputStream stream,
            String fileName) throws IOException;
}
