package com.mjkrt.rendr.controller;

import java.util.logging.Logger;

import com.mjkrt.rendr.entity.SimpleRow;
import com.mjkrt.rendr.service.SimpleRowService;
import com.mjkrt.rendr.utils.LogsCenter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/simpleRow")
public class SimpleRowController {

    private static final Logger LOG = LogsCenter.getLogger(SimpleRowController.class);
    
    @Autowired
    private SimpleRowService simpleRowService;

    @GetMapping("GET /list")
    public Iterable<SimpleRow> list() {
        LOG.info("/simpleRow/list called");
        return simpleRowService.list(); // not sure why it doesn't work
    }
}
