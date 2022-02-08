package com.mjkrt.rendr.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonStreamContext;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.mjkrt.rendr.entity.SimpleRow;
import net.bytebuddy.build.Plugin;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;
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
        ObjectReader reader = mapper.readerFor(new TypeReference<List<SimpleRow>>() {});

        // to get json file from resources folder
        File file = new ClassPathResource("json/Simple.json").getFile();
        JsonNode data = mapper.readTree(file);
        JsonNode body = data.path("body");
        JsonNode report = body.path("SIMPLE_REPORT");
        JsonNode rows = report.path("rows");
        List<SimpleRow> simpleRows = reader.readValue(rows);


        ByteArrayInputStream stream = service.generateWorkBook(simpleRows);
        IOUtils.copy(stream, response.getOutputStream());
    }
}
