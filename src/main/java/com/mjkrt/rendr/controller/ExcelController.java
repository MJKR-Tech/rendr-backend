package com.mjkrt.rendr.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.mjkrt.rendr.entity.DataTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mjkrt.rendr.entity.helper.TemplateIdHolder;
import com.mjkrt.rendr.service.ExcelService;
import com.mjkrt.rendr.utils.LogsCenter;

/**
 * ExcelController.
 * 
 * This class provides the endpoints required for users and front-end application to tap onto.
 */
@CrossOrigin(origins = "${local.front.end.react.js.origin}")
@RequestMapping("/api/v1")
@RestController
public class ExcelController {

    private static final Logger LOG = LogsCenter.getLogger(ExcelController.class);

    @Value("${upload.sample-file}")
    private String sampleTemplateFileName;
    
    @Autowired
    private ExcelService excelService;

    /**
     * Lists all DataTemplates present.
     * 
     * @return list of DataTemplates
     */
    @GetMapping("/getTemplates")
    public List<DataTemplate> getTemplates() {
        LOG.info("GET /getTemplates called");
        
        return excelService.getTemplates();
    }

    /**
     * Uploads an Excel template instance.
     * 
     * @param file Excel template instance to upload and extract
     * @return DataTemplate ID of saved template instance
     */
    @PostMapping("/uploadTemplate")
    public TemplateIdHolder uploadExcel(@RequestParam("file") MultipartFile file) {
        LOG.info("POST /uploadTemplate called");
        
        return excelService.uploadTemplateFromFile(file);
    }

    /**
     * Delete DataTemplates based on list of IDs provided
     * 
     * @param templateIds list of DataTemplate IDs
     * @return true if successful, else false
     */
    @DeleteMapping("/deleteTemplate/{ids}")
    public boolean deleteTemplate(@PathVariable("ids") List<Long> templateIds) {
        LOG.info("DELETE /deleteTemplate/" + templateIds + " called");
        
        return excelService.deleteTemplate(templateIds);
    }

    /**
     * Provides the sample template used in the application.
     * 
     * @param response HTTP response sent to user
     * @throws IOException if writing Excel file to response fails or becomes corrupt
     */
    @GetMapping("/downloadSampleTemplate")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        LOG.info("POST /downloadTemplate called");
        
        ByteArrayInputStream stream = excelService.getSampleTemplate();
        excelService.copyByteStreamToResponse(response, stream, sampleTemplateFileName);
    }

    /**
     * Downloads the DataTemplate specified by its ID.
     * 
     * @param response HTTP response sent to user
     * @param templateIdHolder DataTemplate ID in question
     * @throws IOException if writing Excel file to response fails or becomes corrupt
     */
    @PostMapping("/downloadTemplate")
    public void downloadTemplate(HttpServletResponse response, @RequestBody TemplateIdHolder templateIdHolder)
            throws IOException {
        
        LOG.info("POST /downloadTemplate called");
        
        String fileName = excelService.getFileNameForTemplate(templateIdHolder.getTemplateId());
        ByteArrayInputStream stream = excelService.getTemplate(templateIdHolder.getTemplateId());
        excelService.copyByteStreamToResponse(response, stream, fileName);
    }

    /**
     * Generates the Excel with data specified in the JSON body.
     * The JSON body includes "fileName", "jsonObjects" and "templateID".
     * 
     * @param response HTTP response sent to user
     * @param json json data body to used for Excel data population
     * @throws IOException if writing Excel file to response fails or becomes corrupt
     */
    @PostMapping("/generateData")
    public void generateData(HttpServletResponse response, @RequestBody JsonNode json) throws IOException {
        LOG.info("POST /generateData called");
        
        String fileName = json.get("fileName").textValue();
        ByteArrayInputStream stream = excelService.generateExcel(json);
        excelService.copyByteStreamToResponse(response, stream, fileName);
    }
}
