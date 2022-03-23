package com.mjkrt.rendr.service;

import com.mjkrt.rendr.entity.SimpleRow;
import com.mjkrt.rendr.repository.SimpleRowRepository;
import com.mjkrt.rendr.utils.LogsCenter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class SimpleRowService {
    private static final Logger LOG = LogsCenter.getLogger(SimpleRowService.class);

    @Autowired
    private SimpleRowRepository simpleRowRepository;

    public Iterable<SimpleRow> list() {
        LOG.info("Getting all simple rows");
        return simpleRowRepository.findAll();
    }

    public SimpleRow save(SimpleRow simpleRow) {
        LOG.info("Saving simple row " + simpleRow);
        return simpleRowRepository.save(simpleRow);
    }

    public Iterable<SimpleRow> save(List<SimpleRow> simpleRows) {
        LOG.info("Saving simple " + simpleRows.size() + " rows in bulk");
        return simpleRowRepository.saveAll(simpleRows);
    }
}
