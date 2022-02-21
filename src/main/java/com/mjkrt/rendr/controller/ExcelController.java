package com.mjkrt.rendr.controller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.mjkrt.rendr.entity.ColumnHeader;
import com.mjkrt.rendr.entity.SimpleRow;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
        LOG.info("GET /hello called");
        return "Hello World!";
    }
    
    @PostMapping("/loadSampleData")
    public void loadSampleExcel(HttpServletResponse response, @RequestBody JsonNode jsonNode) throws IOException {
        LOG.info("GET /loadSampleExcel called");
        
        String fileName = "sampleData";
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xlsx");

        ObjectMapper mapper = new ObjectMapper();
        ObjectReader reader = mapper.readerFor(new TypeReference<List<ColumnHeader>>() {});

        // to get json file from resources folder
        JsonNode body = jsonNode.path("body");
        List<JsonNode> bodyChildren = body.findParents("rows");
        JsonNode columns = bodyChildren.get(0).path("columns");
        JsonNode rows = bodyChildren.get(0).path("rows");
        List<ColumnHeader> simpleColumns = reader.readValue(columns);

        Iterator<JsonNode> children = rows.elements();
        final List<JsonNode> childrenList = new ArrayList<>();
        while (children.hasNext()) {
            childrenList.add(children.next());
        }

        ByteArrayInputStream stream = service.generateExcel(simpleColumns, childrenList);
        IOUtils.copy(stream, response.getOutputStream());
    }
}
