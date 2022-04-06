package com.mjkrt.rendr.service.template;

import java.util.List;

import com.mjkrt.rendr.entity.DataCell;
import com.mjkrt.rendr.entity.DataTable;
import com.mjkrt.rendr.entity.DataTemplate;

public interface DataTemplateService {
    List<DataTemplate> listAll();
    List<Long> listAllIds();
    DataTemplate findById(long id);
    boolean isPresent(long id);
    DataTemplate save(DataTemplate dataTemplate);
    void deleteById(long id);
    void deleteAll();
    List<DataTable> findDataTablesWithTemplateId(long id);
    List<DataCell> findDataCellsWithTemplateId(long id);
}
