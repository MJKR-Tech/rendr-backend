package com.mjkrt.rendr.service;

import com.mjkrt.rendr.entity.DataTemplate;
import com.mjkrt.rendr.repository.DataTemplateRepository;
import com.mjkrt.rendr.utils.LogsCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class DataTemplateServiceImpl implements DataTemplateService {
    
    private static final Logger LOG = LogsCenter.getLogger(DataTemplate.class);

    @Autowired
    private DataTemplateRepository dataTemplateRepository;
    
    @Override
    public List<DataTemplate> listAll() {
        LOG.info("Listing all dataTemplates");
        Sort sortByTemplateIdAsc = Sort.by(Sort.Direction.ASC, "templateId");
        return dataTemplateRepository.findAll(sortByTemplateIdAsc);
    }

    @Override
    public DataTemplate findById(long id) {
        LOG.info("Finding dataTemplate by id " + id);
        return dataTemplateRepository.getById(id);
    }

    @Override
    public DataTemplate save(DataTemplate dataTemplate) {
        LOG.info("Saving dataTemplate " + dataTemplate);
        return dataTemplateRepository.save(dataTemplate);
    }

    @Override
    public void deleteById(long id) {
        LOG.info("Deleting dataTemplate by id " + id);
        dataTemplateRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        dataTemplateRepository.deleteAll();
    }
}
