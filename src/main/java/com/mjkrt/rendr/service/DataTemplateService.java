package com.mjkrt.rendr.service;

import java.util.List;

import com.mjkrt.rendr.entity.DataTemplate;

public interface DataTemplateService {
    List<DataTemplate> listAll();
    DataTemplate findById(long id);
    DataTemplate save(DataTemplate dataTemplate);
    void deleteById(long id);
}
