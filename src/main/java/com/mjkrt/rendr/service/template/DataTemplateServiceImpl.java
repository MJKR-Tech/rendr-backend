package com.mjkrt.rendr.service.template;

import com.mjkrt.rendr.entity.DataCell;
import com.mjkrt.rendr.entity.DataSheet;
import com.mjkrt.rendr.entity.DataTable;
import com.mjkrt.rendr.entity.DataTemplate;
import com.mjkrt.rendr.repository.DataTemplateRepository;
import com.mjkrt.rendr.utils.LogsCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * DataTemplateServiceImpl.
 * 
 * This class helps to implement the services required in DataTemplateService.
 */
@Service
public class DataTemplateServiceImpl implements DataTemplateService {
    
    private static final Logger LOG = LogsCenter.getLogger(DataTemplate.class);

    @Autowired
    private DataTemplateRepository dataTemplateRepository;

    /**
     * @inheritDoc
     */
    @Override
    public List<DataTemplate> listAll() {
        LOG.info("Listing all dataTemplates");
        Sort sortByTemplateIdAsc = Sort.by(Sort.Direction.ASC, "templateId");
        return dataTemplateRepository.findAll(sortByTemplateIdAsc);
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<Long> listAllIds() {
        LOG.info("Listing all dataTemplateIds");
        Sort sortByTemplateIdAsc = Sort.by(Sort.Direction.ASC, "templateId");
        return dataTemplateRepository.findAll(sortByTemplateIdAsc).stream()
                .map(DataTemplate::getTemplateId)
                .collect(Collectors.toList());
    }

    /**
     * @inheritDoc
     */
    @Override
    public DataTemplate findById(long id) {
        LOG.info("Finding dataTemplate by id " + id);
        return dataTemplateRepository.getById(id);
    }

    /**
     * @inheritDoc
     */
    @Override
    public boolean isPresent(long id) {
        LOG.info("Checking existence of dataTemplate by id " + id);
        return dataTemplateRepository.existsById(id);    
    }

    /**
     * @inheritDoc
     */
    @Override
    public DataTemplate save(DataTemplate dataTemplate) {
        LOG.info("Saving dataTemplate " + dataTemplate);
        return dataTemplateRepository.save(dataTemplate);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void deleteById(long id) {
        LOG.info("Deleting dataTemplate by id " + id);
        dataTemplateRepository.deleteById(id);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void deleteAll() {
        LOG.info("Deleting all dataTemplates");
        dataTemplateRepository.deleteAll();
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<DataTable> findDataTablesWithTemplateId(long id) {
        LOG.info("Obtaining dataTables with dataTemplateId " + id);
        if (!isPresent(id)) {
            throw new IllegalArgumentException("Template with given ID is not present");
        }
        
        return dataTemplateRepository.getById(id)
                .getDataSheets()
                .stream()
                .map(DataSheet::getDataTables)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<DataCell> findDataCellsWithTemplateId(long id) {
        LOG.info("Obtaining dataCells with dataTemplateId " + id);
        if (!isPresent(id)) {
            throw new IllegalArgumentException("Template with given ID is not present");
        }
        
        return dataTemplateRepository.getById(id)
                .getDataSheets()
                .stream()
                .map(DataSheet::getDataCells)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<DataSheet> findDataSheetsWithTemplateId(long id) {
        LOG.info("Obtaining dataSheets with dataTemplateId " + id);
        if (!isPresent(id)) {
            throw new IllegalArgumentException("Template with given ID is not present");
        }
        return dataTemplateRepository.getById(id)
                .getDataSheets();
    }
}
