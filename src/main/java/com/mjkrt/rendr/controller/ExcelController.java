package com.mjkrt.rendr.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

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
        ByteArrayInputStream stream = service.generateWorkBook();
        IOUtils.copy(stream, response.getOutputStream());
    }
}
