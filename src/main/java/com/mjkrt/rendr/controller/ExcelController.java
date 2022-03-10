package com.mjkrt.rendr.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.mjkrt.rendr.entity.ColumnHeader;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
