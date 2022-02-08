package com.mjkrt.rendr.controller;

import com.mjkrt.rendr.entity.SimpleRow;
import com.mjkrt.rendr.service.SimpleRowService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/simpleRow")
public class SimpleRowController {
    private SimpleRowService simpleRowService;

    public SimpleRowController(SimpleRowService simpleRowService) {
        this.simpleRowService = simpleRowService;
    }

    @GetMapping("/list")
    public Iterable<SimpleRow> list() {
        return simpleRowService.list();
    }
}
