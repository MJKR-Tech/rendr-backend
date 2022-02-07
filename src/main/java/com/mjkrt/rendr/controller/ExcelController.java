package com.mjkrt.rendr.controller;

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mjkrt.rendr.entity.ExcelForm;
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
    
    @GetMapping("/loadSample")
    public ResponseEntity<?> loadSampleExcel() {
        LOG.info("loadSampleExcel called");
        try {
            Workbook workbook = service.generateWorkBook();
            return ResponseEntity.ok(workbook);
        } catch (IOException ex) {
            LOG.warning("Excel failed to generate: " + ex.getLocalizedMessage());
            return ResponseEntity.internalServerError().body("Unable to generate excel file.");
        }
    }

    @PostMapping("/download")
    public Object getExcel(@RequestBody ExcelForm form) {
        LOG.info("getExcel called");
        try {
            Workbook workbook = service.generateWorkBook();
            return ResponseEntity.ok(workbook);
        } catch (IOException ex) {
            LOG.warning("Excel failed to generate: " + ex.getLocalizedMessage());
            return ResponseEntity.internalServerError().body("Unable to generate excel file.");
        }
    }
}
