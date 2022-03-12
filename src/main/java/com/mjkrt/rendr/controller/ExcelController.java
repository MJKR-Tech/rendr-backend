package com.mjkrt.rendr.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.mjkrt.rendr.entity.ColumnHeader;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mjkrt.rendr.service.ExcelService;
import com.mjkrt.rendr.service.JsonService;
import com.mjkrt.rendr.utils.LogsCenter;

@CrossOrigin(origins = "http://localhost:3000") // TODO remove after merging services together
@RequestMapping("/api/v1")
@RestController
public class ExcelController {

    private static final Logger LOG = LogsCenter.getLogger(ExcelController.class);
    
    @Autowired
    private ExcelService excelService;
    
    @Autowired
    private JsonService jsonService;
    
    @GetMapping("/hello")
    public String greet() {
        LOG.info("GET /hello called");
        return "Hello World!";
    }
    
    @PostMapping("/generateData")
    public void generateData(HttpServletResponse response, @RequestBody JsonNode json) throws IOException {
        LOG.info("POST /generateData called");

        List<ColumnHeader> headers = jsonService.getHeaders(json);
        List<JsonNode> rows = jsonService.getRows(json);
        String fileName = "Sample"; // todo add excel file name in frontend/request
        
        ByteArrayInputStream stream = excelService.generateExcel(fileName, headers, rows);
        
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xlsx");
        IOUtils.copy(stream, response.getOutputStream());
    }
    
    @PostMapping("/uploadExcel")
    public boolean uploadExcel(@RequestParam("file") MultipartFile file) throws IOException {
        LOG.info("POST /uploadExcel called");
        
        LOG.info("File content type: " + file.getContentType());
        List<String> excelTypes = List.of(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // xlsx
                "application/vnd.ms-excel" // xls
        );
        if (file.getContentType() == null || !excelTypes.contains(file.getContentType())) {
            return false;
        }
        
        Workbook workbook = (excelTypes.get(0).equals(file.getContentType()))
                ? new XSSFWorkbook(file.getInputStream())
                : new HSSFWorkbook(file.getInputStream());
        LOG.info("File is excel, closing request");
        
        return true;
    }
}
