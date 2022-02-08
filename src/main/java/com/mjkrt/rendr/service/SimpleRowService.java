package com.mjkrt.rendr.service;

import com.mjkrt.rendr.entity.SimpleRow;
import com.mjkrt.rendr.repository.SimpleRowRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SimpleRowService {

    private SimpleRowRepository simpleRowRepository;

    public SimpleRowService(SimpleRowRepository simpleRowRepository) {
        this.simpleRowRepository = simpleRowRepository;
    }

    public Iterable<SimpleRow> list() {
        return simpleRowRepository.findAll();
    }

    public SimpleRow save(SimpleRow simpleRow) {
        return simpleRowRepository.save(simpleRow);
    }

    public Iterable<SimpleRow> save(List<SimpleRow> simpleRows) {
        return simpleRowRepository.saveAll(simpleRows);
    }

}
