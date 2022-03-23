package com.mjkrt.rendr.service;

import com.mjkrt.rendr.entity.DataTemplate;
import com.mjkrt.rendr.repository.DataTemplateRepository;
import com.mjkrt.rendr.utils.LogsCenter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.logging.Logger;

public class TemplateService {
    private static final Logger LOG = LogsCenter.getLogger(DataTemplate.class);

    @Autowired
    private DataTemplateRepository dataTemplateRepository;


    public DataTemplate save(DataTemplate dataTemplate) {
        LOG.info("Saving dataTemplate " + dataTemplate);
        return dataTemplateRepository.save(dataTemplate);
    }

    public Iterable<DataTemplate> save(List<DataTemplate> dataTemplates) {
        LOG.info("Saving dataTemplates " + dataTemplates.size() + " in bulk");
        return dataTemplateRepository.saveAll(dataTemplates);
    }
}
