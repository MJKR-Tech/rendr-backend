package com.mjkrt.rendr.controller;

import java.util.List;
import java.util.logging.Logger;

import com.mjkrt.rendr.entity.SimpleRow;
import com.mjkrt.rendr.service.SimpleRowService;
import com.mjkrt.rendr.utils.LogsCenter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/simpleRow")
public class SimpleRowController {

    private static final Logger LOG = LogsCenter.getLogger(SimpleRowController.class);
    
    @Autowired
    private SimpleRowService simpleRowService;

    @GetMapping("/list")
    public Iterable<SimpleRow> list() {
        LOG.info("GET /simpleRow/list called");
        return simpleRowService.list(); // not sure why it doesn't work
    }
    
    @PostMapping("add")
    public boolean add(@RequestBody List<SimpleRow> rows) {
        LOG.info("POST /simpleRow/add called");
        simpleRowService.save(rows);
        return true;
    }
}
