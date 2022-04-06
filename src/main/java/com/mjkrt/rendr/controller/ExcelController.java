package com.mjkrt.rendr.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.mjkrt.rendr.entity.DataTable;
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

import com.mjkrt.rendr.entity.helper.ColumnHeader;
import com.mjkrt.rendr.entity.helper.TableHolder;
import com.mjkrt.rendr.service.mapper.DataMapperService;
import com.mjkrt.rendr.service.ExcelService;
import com.mjkrt.rendr.service.mapper.JsonService;
import com.mjkrt.rendr.utils.LogsCenter;

@CrossOrigin(origins = "http://localhost:3000") // todo remove after system test passes
@RequestMapping("/api/v1")
@RestController
public class ExcelController {

    private static final Logger LOG = LogsCenter.getLogger(ExcelController.class);

    @Value("${upload.sample-file}")
    private String sampleTemplateFileName;
    
    @Autowired
    private ExcelService excelService;

    @GetMapping("/getTemplates")
    public List<DataTemplate> getTemplates() {
        LOG.info("GET /getTemplates called");
        
        return excelService.getTemplates();
    }

    @PostMapping("/uploadTemplate")
    public boolean uploadExcel(@RequestParam("file") MultipartFile file) {
        LOG.info("POST /uploadTemplate called");
        
        return excelService.uploadTemplateFromFile(file);
    }

    @DeleteMapping("/deleteTemplate/{id}")
    public boolean deleteTemplate(@PathVariable("id") long templateId) {
        LOG.info("DELETE /deleteTemplate/" + templateId + " called");
        
        return excelService.deleteTemplate(templateId);
    }

    @GetMapping("/downloadSampleTemplate")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        LOG.info("POST /downloadTemplate called");
        
        ByteArrayInputStream stream = excelService.getSampleTemplate();
        excelService.copyByteStreamToResponse(response, stream, sampleTemplateFileName);
    }

    @PostMapping("/downloadTemplate")
    public void downloadTemplate(HttpServletResponse response, @RequestBody long templateId) throws IOException {
        LOG.info("POST /downloadTemplate called");
        
        String fileName = excelService.getFileNameForTemplate(templateId);
        ByteArrayInputStream stream = excelService.getTemplate(templateId);
        excelService.copyByteStreamToResponse(response, stream, fileName);
    }

    @PostMapping("/generateData")
    public void generateData(HttpServletResponse response, @RequestBody JsonNode json) throws IOException {
        LOG.info("POST /generateData called");
        
        String fileName = json.get("fileName").textValue();
        ByteArrayInputStream stream = excelService.generateExcel(json);
        excelService.copyByteStreamToResponse(response, stream, fileName);
    }

    /*
        TODO delete all below after testing fully
    */
    
    @Autowired
    private JsonService jsonService;

    @Autowired
    private DataMapperService dataMapperService;

    @DeleteMapping("/deleteAllTemplates")
    public boolean deleteAllTemplates() {
        LOG.info("DELETE /deleteAllTemplates called");
        excelService.deleteAllTemplates();
        return true;
    }

    // todo remove after integrating services
    @PostMapping("/testUploadMapping")
    public Map<Long, TableHolder> generateJsonMapping(@RequestBody JsonNode json) throws IOException {
        LOG.info("POST /generateJsonMapping called");

        long templateId = json.path("templateId").longValue();
        List<ColumnHeader> columnHeaders = jsonService.getHeaders(json.get("jsonObjects"));
        List<JsonNode> rows = jsonService.getRows(json.get("jsonObjects"));
        List<TableHolder> linkedTable = dataMapperService.generateLinkedTableHolders(templateId, columnHeaders, rows);
        Map<Long, TableHolder> map = dataMapperService.generateTableMapping(templateId, columnHeaders, linkedTable);
        return map;
    }
}
