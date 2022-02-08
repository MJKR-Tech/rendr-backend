package com.mjkrt.rendr.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mjkrt.rendr.entity.SimpleRow;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mjkrt.rendr.service.ExcelService;
import com.mjkrt.rendr.utils.LogsCenter;

@RestController
public class ExcelController {

    private static final Logger LOG = LogsCenter.getLogger(ExcelController.class);
    
    @Autowired
    private ExcelService service;
    
    @GetMapping("/hello")
    public String greet() {
        LOG.info("greeting called");
        return "Hello World!";
    }
    
    @GetMapping("/loadSampleData")
    public void loadSampleExcel(HttpServletResponse response) throws IOException {
        LOG.info("loadSampleExcel called");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=sampleData.xlsx");

        ObjectMapper mapper = new ObjectMapper();
        TypeReference<List<SimpleRow>> typeReference = new TypeReference<>(){};
        InputStream inputStream = TypeReference.class.getResourceAsStream("/json/Simple.json");
        List<SimpleRow> simpleRows = mapper.readValue(inputStream, typeReference);

        ByteArrayInputStream stream = service.generateWorkBook(simpleRows);
        IOUtils.copy(stream, response.getOutputStream());
    }
}
